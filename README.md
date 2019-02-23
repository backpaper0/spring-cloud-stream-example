# Spring Cloud Stream

## 遊ぶために必要なもの

- Java 11
- Maven 3
- Docker
- curl

## RabbitMQを起動する(via Docker)

管理画面を見たいので`-management`が付いているバージョンを使用する。

```sh
docker run -d --name mq -h usaq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

次のURLで管理画面を開く。

- http://localhost:15672/

ユーザー名・パスワードはデフォルトだとどちらも`guest`。

## アプリケーションをビルドする

```sh
mvn package
```

## メッセージ送信側アプリケーションを起動する

HTTPで受け取った名前を`Person`にセットしてキューへ送信するアプリケーション。

```sh
java -jar source-app/target/source-app.jar
```

## メッセージ受信側アプリケーションを起動する

キューから受信した`Person`を標準出力へ書き出すアプリケーション。

```sh
java -jar sink-app/target/sink-app.jar
```

## メッセージを送信する

`source-app`へHTTPで`name`を送る。

```sh
curl localhost:8080 -H "Content-Type: application/json" -d '{"name":"hoge"}'
```

そうすると、`SourceApp#handle`がHTTPリクエストを受け取って`sample` exchangeへメッセージを送信する。

ここで送信先となるexchangeは`application.properties`に書かれた`spring.cloud.stream.bindings.output.destination`の値で設定される。
デフォルトだとbinding target name、つまり今回だと`output`になる。

exchangeへ送信されたメッセージはバインドされているキューへ送信される。

キューはデフォルトだと`sink-app`のインスタンス毎に1つ用意されるが、グループが設定されている場合はグループ毎に1つ用意される。
グループは`application.properties`の`spring.cloud.stream.bindings.input.group`の値で設定される。

キューがどのexchangeへバインドされるかは`application.properties`に書かれた`spring.cloud.stream.bindings.input.destination`の値で設定される。
デフォルトだとbinding target name、つまり今回だと`input`になる。

`SinkApp#handle`に受信したメッセージが渡され、標準出力に書き出される。

### 参考

- binding target name: `org.springframework.cloud.stream.annotation.Output.value()`
- binding target name: `org.springframework.cloud.stream.annotation.Input.value()`
- `org.springframework.cloud.stream.config.BindingServiceProperties`
- `org.springframework.cloud.stream.config.BindingProperties`

## 冗長化

RabbitMQクラスタを構築する。

まず1つめのRabbitMQを起動する。

```sh
docker run -d -h usaq1 --name mq1 -e RABBITMQ_ERLANG_COOKIE=secret -p 15672:15672 rabbitmq:3-management
```

次に2つめのRabbitMQを起動して`docker exec`で`bash`を起動する。

```sh
docker run -d -h usaq2 --name mq2 -e RABBITMQ_ERLANG_COOKIE=secret --link mq1 rabbitmq:3-management
docker exec -it mq2 bash
```

`rabbitmqctl`コマンドを使用してクラスタを構築する。

```sh
rabbitmqctl stop_app
rabbitmqctl join_cluster rabbit@usaq1
rabbitmqctl start_app
rabbitmqctl set_policy ha-two "^sample" '{"ha-mode":"exactly","ha-params":2,"ha-sync-mode":"automatic"}'
exit
```

メッセージ受信アプリケーションを複数起動する。

```sh
docker run -d --name sink-app1 --link mq1 --link mq2 -v `pwd`/sink-app/target/sink-app.jar:/app.jar openjdk:11 java -jar /app.jar --spring.rabbitmq.addresses=mq1:5672,mq2:5672
docker run -d --name sink-app2 --link mq1 --link mq2 -v `pwd`/sink-app/target/sink-app.jar:/app.jar openjdk:11 java -jar /app.jar --spring.rabbitmq.addresses=mq1:5672,mq2:5672
```

メッセージ送信アプリケーションを複数起動する。

```sh
docker run -d --name source-app1 --link mq1 --link mq2 -v `pwd`/source-app/target/source-app.jar:/app.jar openjdk:11 java -jar /app.jar --spring.rabbitmq.addresses=mq1:5672,mq2:5672
docker run -d --name source-app2 --link mq1 --link mq2 -v `pwd`/source-app/target/source-app.jar:/app.jar openjdk:11 java -jar /app.jar --spring.rabbitmq.addresses=mq1:5672,mq2:5672
```

ロードバランサーを起動する。

```sh
docker run -d --name lb --link source-app1 --link source-app2 -v `pwd`/lb/default.conf:/etc/nginx/conf.d/default.conf -p 8080:80 nginx
```

ロードバランサーを経由して`source-app`へHTTPで`name`を送る。

```sh
curl localhost:8080 -H "Content-Type: application/json" -d '{"name":"hoge"}'
```

### 連続でリクエストを投げながら色々止めたりしながら遊ぼう

連続でリクエスト投げる。

```sh
for i in `seq 10000`; do curl localhost:8080 -H "Content-Type: application/json" -d '{"name":"hoge-'$i'"}'; sleep 1; done
```

色々止める

```sh
docker stop source-app1
docker stop sink-app1
docker stop mq1
```

