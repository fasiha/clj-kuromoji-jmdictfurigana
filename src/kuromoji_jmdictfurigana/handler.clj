(ns kuromoji-jmdictfurigana.handler
  (:import java.net.URLEncoder)
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]
            [ring.util.response :refer [file-response]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.cors :refer [wrap-cors]]
            [kuromoji-jmdictfurigana.kuromoji :as kuromoji]))

(defroutes handler
  (GET
   "/parse-nbest/:text"
   [text]
   (do (println "Parsing text (n-best):" text)
     {:body (kuromoji/parse-nbest text 10)}))

  (GET
   "/parse/:text"
   [text]
   (do (println "Parsing text:" text)
     {:body (kuromoji/parse text)}))

  (GET
   "/parse-furigana/:text"
   [text]
   (do (println "Parsing text (furigana):" text)
     {:body (kuromoji/parse-with-furigana text)}))


  (route/not-found "Not found."))

(def app (-> #'handler
             (wrap-cors :access-control-allow-origin [#".*"]
                        :access-control-allow-methods [:get])
             (wrap-restful-format)))
