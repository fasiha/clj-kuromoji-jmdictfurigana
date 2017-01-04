(defproject kuromoji-jmdictfurigana "0.1.0-SNAPSHOT"
  :description "Kuromoji is great. JmdictFurigana is great. Wouldn’t it be great if they got together?"
  :url "https://github.com/fasiha/clj-kuromoji-jmdictfurigana"
  :license {:name "Unlicense"
            :url "http://unlicense.org/"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.4.0"]
                 [ring "1.4.0"]
                 [com.cognitect/transit-clj "0.8.288"]
                 [ring/ring-defaults "0.2.0"]
                 [ring-middleware-format "0.7.0"]
                 [ring/ring-mock "0.3.0"]
                 [cheshire "5.5.0"]
                 [clj-time "0.12.0"]
                 [clj-http "2.2.0"]
                 [ring-cors "0.1.8"]
                 ]
  :plugins [[lein-ring "0.10.0"]
            [lein-expand-resource-paths "0.0.1"]]
  :ring {:handler kuromoji-jmdictfurigana.handler/app
         :port 3600}
  :resource-paths ["resources/jars-cc64f5fdda8/*"]
  :main ^:skip-aot kuromoji-jmdictfurigana.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
