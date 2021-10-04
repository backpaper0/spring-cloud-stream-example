# Spring Cloud Stream

## 遊ぶために必要なもの

- Java 11
- Docker
- curl

## RabbitMQを起動する(via Docker)

管理画面を見たいので`-management`が付いているバージョンを使用する。

```sh
docker run -d --name mq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

次のURLで管理画面を開く。

- http://localhost:15672/

ユーザー名・パスワードはデフォルトだとどちらも`guest`。

## メッセージ送信側アプリケーションを起動する

HTTPで受け取った名前を`Person`にセットしてキューへ送信するアプリケーション。

```sh
cd source-app
./mvnw spring-boot:run
```

## メッセージ受信側アプリケーションを起動する

キューから受信した`Person`を標準出力へ書き出すアプリケーション。

```sh
cd sink-app
./mvnw spring-boot:run
```

## メッセージを送信する

`source-app`へHTTPで`name`を送る。

```sh
curl localhost:8080 -H "Content-Type: application/json" -d '{"name":"hoge"}'
```

そうすると、`SourceApp#handle`がHTTPリクエストを受け取って`person`という名前のExchangeへメッセージを送信する。

ここで送信先となるExchangeは`StreamBridge#send`の第1引数によって指定される。

Exchangeへ送信されたメッセージはバインドされているキューへ送信される。

キューはデフォルトだと`sink-app`のインスタンス毎に1つ用意されるが、グループが設定されている場合はグループ毎に1つ用意される。
グループは`spring.cloud.stream.bindings.<bindingName>.group`の値で設定される。

`SinkApp#handle`に受信したメッセージが渡され、標準出力に書き出される。

## 冗長化

RabbitMQクラスタを構築してSpring Cloud Streamを試してみる。

必要なコマンドは`Makefile`にまとめている。

まずアプリケーションのコンテナイメージをビルドする。

```sh
make build
```

次にDocker ComposeでRabbitMQ、アプリケーション、ロードバランサー(Nginx)を起動する。

```sh
make up
```

サービスの起動には少し時間がかかる。


サービスが起動したらロードバランサーを経由して`source-app`へHTTPで`name`を送る。

```sh
make demo1
```

### 連続でリクエストを投げながら色々止めたりしながら遊ぼう

連続でリクエスト投げる。

```sh
make demo2
```

### 後始末

Docker Composeを落とす。

```
make down
```

コンテナイメージを破棄する。

```
make destroy
```
