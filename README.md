# Use [Kuromoji](https://github.com/atilika/kuromoji) 1.0 and UniDic from JVM Clojure

This repo builds on the Kuromoji 1.0 branch of my [`demo-clojure-kuromoji`](https://github.com/fasiha/demo-clojure-kuromoji/tree/kuromoji-1.0) repo. It contains a JAR containing a snapshot of Kuromoji 1.0, which it makes available as a web microservice, using the powerful UniDic dictionary.

## Install
Follow instructions for lein at http://leiningen.org/#install.

A simple demo is available: clone this repo, `cd` into it, and run `lein run` to see the best and the top-3 tokenizations of “何できた？”. Read `src/kuromoji_front_end/core.clj` to see how straightforward the API is.

## Run microservice
Clone this repo, `cd` into it, then:
```
$ lein trampoline ring server-headless
```
This starts the webserver on port 3600 (change by editing `project.clj`, specifically, `{:ring {:port 3600}}`).

Test, asking for HTML output:
```
$ curl -v -H "Accept: text/html" http://localhost:3600/parse/何できた？
> GET /parse/何できた？ HTTP/1.1
> Host: localhost:3600
> User-Agent: curl/7.51.0
> Accept: text/html
>
< HTTP/1.1 200 OK
< Date: Tue, 03 Jan 2017 03:19:23 GMT
< Content-Type: text/html; charset=UTF-8
< Content-Length: 3045
< Server: Jetty(9.2.10.v20150310)
<
<html>
<head></head>
<body><div><pre>
- language-type: 和
  part-of-speech: [pronoun]
  final-sound-alternation-type: "*"
  known?: true
  lemma-pronunciation: ナン
  written-base-form: 何
  lemma-reading: ナニ
  written-form: 何
  lemma: 何
  initial-sound-alternation-form: "*"
  final-sound-alternation-form: "*"
  user?: false
  conjugation-type: []
  conjugation: [uninflected]
  literal: 何
  initial-sound-alternation-type: "*"
  all-features: [代名詞, "*", "*", "*", "*", "*", ナニ, 何, 何, ナン, 何, ナン, 和, "*", "*", "*",
    "*"]
  position: 0
  literal-pronunciation: ナン
- language-type: 和
  part-of-speech: [verb, bound]
  final-sound-alternation-type: "*"
  known?: true
  lemma-pronunciation: デキル
  written-base-form: できる
  lemma-reading: デキル
  written-form: でき
  lemma: 出来る
  initial-sound-alternation-form: "*"
  final-sound-alternation-form: "*"
  user?: false
  conjugation-type: [kamiichidan-verb-i-row, ka-column]
  conjugation: [continuative, general]
  literal: でき
  initial-sound-alternation-type: "*"
  all-features: [動詞, 非自立可能, "*", "*", 上一段-カ行, 連用形-一般, デキル, 出来る, でき, デキ, できる, デキル,
    和, "*", "*", "*", "*"]
  position: 1
  literal-pronunciation: デキ
- language-type: 和
  part-of-speech: [auxiliary-verb]
  final-sound-alternation-type: "*"
  known?: true
  lemma-pronunciation: タ
  written-base-form: た
  lemma-reading: タ
  written-form: た
  lemma: た
  initial-sound-alternation-form: "*"
  final-sound-alternation-form: "*"
  user?: false
  conjugation-type: [auxiliary, ta]
  conjugation: [conclusive, general]
  literal: た
  initial-sound-alternation-type: "*"
  all-features: [助動詞, "*", "*", "*", 助動詞-タ, 終止形-一般, タ, た, た, タ, た, タ, 和, "*", "*",
    "*", "*"]
  position: 3
  literal-pronunciation: タ
- language-type: 記号
  part-of-speech: [supplementary-symbol, period]
  final-sound-alternation-type: "*"
  known?: true
  lemma-pronunciation: ""
  written-base-form: ？
  lemma-reading: ""
  written-form: ？
  lemma: ？
  initial-sound-alternation-form: "*"
  final-sound-alternation-form: "*"
  user?: false
  conjugation-type: []
  conjugation: [uninflected]
  literal: ？
  initial-sound-alternation-type: "*"
  all-features: [補助記号, 句点, "*", "*", "*", "*", "", ？, ？, "", ？, "", 記号, "*", "*",
    "*", "*"]
  position: 4
  literal-pronunciation: ""
</pre></div></body></html>
```
Here we explicitly asked for HTML, via the `Accept` header. The webserver can also return JSON (`Accept: application/json`) and [Transit](https://github.com/cognitect/transit-format) (`Accept: application/transit+json`), for Clojure/ClojureScript and related consumers.

Note that the Japanese feature names have been replaced by the English translations:

- [part-of-speech features](https://gist.github.com/masayu-a/e3eee0637c07d4019ec9)
- [inflection features](https://gist.github.com/masayu-a/3e11168f9330e2d83a68)
- [inflection type features](https://gist.github.com/masayu-a/b3ce862336e47736e84f)

An awesome feature of Kuromoji 1.0 is N-best tokenizations. This is made available at the `/parse-nbest/` endpoint (i.e., `http://localhost:3600/parse-nbest/何できた？`), and returns the top ten results as an array/vector.

## Abbreviated tokenization
Here’s a subset of the tokenized data for easier digestion, of Kuromoji/UniDic’s tokenization of 「お寿司が食べたい。」.

| literal   | lemma    | part of speech                    | conjugation                | conjugation type                       |
|---|---|---|---|---|
| お        | 御       | [:prefix]                         | [:uninflected]             | []                                     |
| 寿司      | 寿司     | [:noun :common :general]          | [:uninflected]             | []                                     |
| が        | が       | [:particle :case]                 | [:uninflected]             | []                                     |
| 食べ      | 食べる   | [:verb :general]                  | [:continuative :general]   | [:shimoichidan-verb-e-row :ba-column]  |
| たい      | たい     | [:auxiliary-verb]                 | [:conclusive :general]     | [:auxiliary :tai]                      |
| 。        | 。       | [:supplementary-symbol :period]   | [:uninflected]             | []                                     |

## Notes on building Kuromoji 1.0-SNAPSHOT
The JARs included in this repo may be outdated—I am using `cc64f5fdda8` (Nov 16, 2016), and you can check the latest log at [Atilika Kuromoji log](https://github.com/atilika/kuromoji/commits/master).

Here are my notes on building Kuromoji from source.

First, install [Maven](http://maven.apache.org/install.html) (or just `$ brew install maven` on macOS). Then:
```
# In a top-level directory, outside this repo
$ git clone https://github.com/atilika/kuromoji.git
$ cd kuromoji
$ mvn package -DskipDownloadWikipedia -pl kuromoji-unidic -am
$ mkdir /PATH/TO/CLOJURE/PROJECT/resources/jars
$ cp -p `find -name "*.jar"` /PATH/TO/CLOJURE/PROJECT/resources/jars/
```
I appended the HEAD commit to the name of the `jars` directory, so the JARs go to `resources/jars-cc64f5fdda8`. See http://stackoverflow.com/q/2404426/500207 for alternatives.
