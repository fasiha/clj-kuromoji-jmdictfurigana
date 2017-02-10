# Kuromoji 💛 JmdictFurigana

A REST-ful interface to [**Atilika Kuromoji**](http://www.atilika.org/)+UniDic, augmented by the [**JmdictFurigana**](https://github.com/Doublevil/JmdictFurigana/) data set.

This repo contains pre-compiled JARs for Kuromoji with the powerful UniDic dictionary, and is ready to run as-is. (If you want stand-alone microservices for either of these, check out [clj-kuromoji-front-end](https://github.com/fasiha/clj-kuromoji-front-end) and [JmdictFurigana-microservice](https://github.com/fasiha/JmdictFurigana-microservice).)

## Installation and operation
First, [install lein](http://leiningen.org/#install).

Then, clone this repo, `cd` into it, and start the webserver on port 3600 (change by editing `project.clj`, specifically, `{:ring {:port 3600}}`):
```
$ git clone https://github.com/fasiha/clj-kuromoji-jmdictfurigana.git
$ cd clj-kuromoji-jmdictfurigana
$ lein trampoline ring server-headless
```

Two GET endpoints are made available: `/parse/<Japanese goes here>` and `/parse-nbest/<Japanese goes here>`, i.e.,:

- [http://localhost:3600/parse/何できた？](http://localhost:3600/parse/何できた？) returns the best tokenization for the string `何できた？`.
- [http://localhost:3600/parse-nbest/何できた？](http://localhost:3600/parse-nbest/何できた？) returns the *ten* best tokenizations (new in Kuromoji 1.0).

The server can return the following MIME types:

- `text/html`: nice for testing with your browser,
- `application/json`: for your AJAX calls,
- `application/transit+json`: [Transit](https://github.com/cognitect/transit-format) for your Clojure/ClojureScript apps.

## Examples
Asking for HTML output:
```
$ curl -v -H "Accept: text/html" http://localhost:3600/parse/何できた？
> GET /parse/何できた？ HTTP/1.1
> Host: localhost:3600
> User-Agent: curl/7.51.0
> Accept: text/html
>
< HTTP/1.1 200 OK
< Date: Thu, 05 Jan 2017 06:43:08 GMT
< Content-Type: text/html; charset=UTF-8
< Content-Length: 3136
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
  furigana:
  - {ruby: 何, rt: なに}
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
  furigana: null
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
  furigana: null
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
  furigana: null
  position: 4
  literal-pronunciation: ""
</pre></div></body></html>
```

Note that, for morpheme literals containing kanji, the `furigana` slot is automatically populated with the morpheme’s literal’s JmdictFurigana results. This is slightly incorrect at times, as you can see here: 何 here is abbreviated to なん, as shows by the `literal-pronunciation` of the first morpheme, but the `furigana` gives the full なに. Help on this is much appreciated!

Also note that the Japanese feature names from the UniDic dictionary have been replaced by the English translations: we follow the following resources, from the BCCWJ authors:

- [part-of-speech features](https://gist.github.com/masayu-a/e3eee0637c07d4019ec9)
- [inflection features](https://gist.github.com/masayu-a/3e11168f9330e2d83a68)
- [inflection type features](https://gist.github.com/masayu-a/b3ce862336e47736e84f)

## Abbreviated tokenization
Here’s a subset of the tokenized data for easier digestion, of Kuromoji/UniDic’s tokenization of 「お寿司が食べたい。」, including the literal’s furigana, if applicable, via the JmdictFurigana database.

| literal | lemma | part of speech | conjugation | conjugation type | furigana |
|---|---|---|---|---|---|
| お | 御 | prefix | uninflected |  |  |
| 寿司 | 寿司 | noun/common/general | uninflected |  | [{"ruby":"寿", "rt":"す"}, {"ruby":"司", "rt":"し"}] |
| が | が | particle/case | uninflected |  |  |
| 食べ | 食べる | verb/general | continuative/general | shimoichidan-verb-e-row/ba-column | [{"ruby":"食"," rt":"た"},"べ"] |
| たい | たい | auxiliary-verb | conclusive/general | auxiliary/tai |  |
| 。 | 。 | supplementary-symbol/period | uninflected |  |  |

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
