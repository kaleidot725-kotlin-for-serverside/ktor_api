# Ktor で簡単な API を作ってみる

## はじめに

Ktor を利用すれば Web API を作成することができる。今回は一番簡単な API をKtor で作成してみたいと思います。API は Restfull であるなど気にしなければ行けないことはたくさんあると思いますが今回はそういうのは一旦省いてやってみようかなと思います。

## ルーティング

ルーティングを利用するには routing と呼ばれる DSL を記述する。

この routing の block の中に get や post という DSL を記述していき API を定義していく。

```kotlin
routing {
    get("/snippets") {
        call.respond(SnippetsDataFactory.SNIPPETS_DATA)
    }
    post("/snippets") {
        SnippetsDataFactory.SNIPPETS_DATA += call.receive<Snippets>()
        call.respond(mapOf("OK" to true))
    }
}
```

-----

ちなみに DSL 次のような定義になっていて、Feature ごとにインストールして使うような構成になっている。

```kotlin
@ContextDsl
fun Application.routing(configuration: Routing.() -> Unit): Routing =
    featureOrNull(Routing)?.apply(configuration) ?: install(Routing, configuration)
```

---

## JSON を返せるようにする

JSON を返せるようにするには Content Negotiation をインストールして jackson を使うように記述してやる。

```kotlin
install(ContentNegotiation) {
    jackson {
    }
}
```

jackson を使うには build.gradle の dependencies に jackson を追加する必要があるので注意する。

```
dependencies {
    implementation "io.ktor:ktor-jackson:$ktor_version"
}
```

---

ちなみに[Content Negotiation](https://developer.mozilla.org/ja/docs/Web/HTTP/Content_negotiation) は HTTP に規定されている仕様らしいです。URLにアクセスする際に例えばブラウザがメディアタイプ、言語、文字セット、エンコーディングなどの情報を指定すると、その情報に基づいて最適なリソースを返すための仕組みっぽい。

![](https://mdn.mozillademos.org/files/13789/HTTPNego.png)

---

## GETメソッドを登録する

次のように get の引数に パスを渡すことで API を定義する。次の例だと /snippets にアクセスしたときの API を作成することになります。get の block にて応答データを生成できるようになっています。（call.respond の引数には Map や List を渡せる。Map を渡すと Key と Value のペアを JSON に変換したもの、List を渡すと Item を JSON に変換したものが返される。）

```kotlin
data class Snippets(val title: String, val detail: String)

object SnippetsDataFactory {
    val SNIPPETS_DATA = listOf(
        Snippets("ONE TITLE", "ONE DETAIL"),
        Snippets("TWO TITLE", "TWO DETAIL"),
        Snippets("THREE TITLE", "THREE DETAIL"),
        Snippets("FOUR TITLE", "FOUR DETAIL")
    )
}

routing {
    get("/snippets") {
        call.respond(SnippetsDataFactory.SNIPPETS_DATA)
    }
}
```

定義した  API  の GET メソッドを呼び出すと次のような結果になります。

```http
GET http://0.0.0.0:8080/snippets
200
50 ms
GET /snippets HTTP/1.1
User-Agent: PostmanRuntime/7.26.1
Accept: */*
Postman-Token: c64e7318-b65e-4429-b7ff-f0b71e3997ef
Host: 0.0.0.0:8080
Accept-Encoding: gzip, deflate, br
Connection: keep-alive
HTTP/1.1 200 OK
Content-Length: 227
Content-Type: application/json; charset=UTF-8
Connection: keep-alive
[{"title":"ONE TITLE","detail":"ONE DETAIL"},{"title":"TWO TITLE","detail":"TWO DETAIL"},{"title":"THREE TITLE","detail":"THREE DETAIL"},{"title":"FOUR TITLE","detail":"FOUR DETAIL"},{"title":"NEW_TITLE","detail":"NEW_DETAIL"}]
```

## POSTメソッドを登録する

次のように post の引数に パスを渡すことで API を定義する。次の例だと /snippets にアクセスしたときの API を作成することになります。post の block にて引き渡されたデータに応じて処理を実行できるようになっている。

```kotlin
routing {
    post("/snippets") {
        SnippetsDataFactory.SNIPPETS_DATA += call.receive<Snippets>()
        call.respond(mapOf("OK" to true))
    }
}
```

定義した API の POST メソッドを呼び出すと次のような結果になります。

```http
POST http://0.0.0.0:8080/snippets
200
85 ms
POST /snippets HTTP/1.1
Content-Type: application/json
User-Agent: PostmanRuntime/7.26.1
Accept: */*
Postman-Token: 443d6f72-7fe7-494c-a7b9-da6a9dae5caf
Host: 0.0.0.0:8080
Accept-Encoding: gzip, deflate, br
Connection: keep-alive
Content-Length: 56
{
    "title": "NEW_TITLE",
    "detail": "NEW_DETAIL"
}
HTTP/1.1 200 OK
Content-Length: 11
Content-Type: application/json; charset=UTF-8
Connection: keep-alive
{"OK":true}
```

## おわりに

- API を作成するには routing をインストールする必要がある。
- API にて JSON の生成・解析処理を利用するには jackson をインストールする必要がある。
- API を定義するには get や post などの DSL 定義を用いる。