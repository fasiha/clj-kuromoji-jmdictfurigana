(ns kuromoji-jmdictfurigana.kuromoji
  (:require [clojure.string :as string]
            [kuromoji-jmdictfurigana.furigana :as furigana]
            [kuromoji-jmdictfurigana.kana :as kana])
  (:import [com.atilika.kuromoji.unidic Token Tokenizer]))

; see https://gist.github.com/masayu-a/e3eee0637c07d4019ec9
(def keywordize-pos
  {"代名詞"         :pronoun
   "副詞"            :adverb
   "助動詞"          :auxiliary-verb
   "助詞"            :particle
   "係助詞"          :binding
   "副助詞"          :adverbial
   "接続助詞"        :conjunctive
   "格助詞"          :case
   "準体助詞"        :nominal
   "終助詞"          :phrase-final
   "動詞"            :verb
   "一般"            :general
   "非自立可能"      :bound
   "名詞"            :noun
   "助動詞語幹"      :auxiliary
   "固有名詞"        :proper
   "人名"            :name
   "名"              :firstname
   "姓"              :surname
   "地名"            :place
   "国"              :country
   "数詞"            :numeral
   "普通名詞"        :common
   "サ変可能"        :verbal-suru
   "サ変形状詞可能"  :verbal-adjectival
   "副詞可能"        :adverbial-suffix
   "助数詞可能"      :counter
   "形状詞可能"      :adjectival
   "形容詞"          :adjective-i
   "形状詞"          :adjectival-noun
   "タリ"            :tari
   "感動詞"          :interjection
   "フィラー"        :filler
   "接尾辞"          :suffix
   "動詞的"          :verbal
   "名詞的"          :nominal-suffix
   "助数詞"          :counter-suffix
   "形容詞的"        :adjective-i-suffix
   "形状詞的"        :adjectival-noun-suffix
   "接続詞"          :conjunction
   "接頭辞"          :prefix
   "空白"            :whitespace
   "補助記号"        :supplementary-symbol
   "ＡＡ"            :ascii-art
   "顔文字"          :emoticon
   "句点"            :period
   "括弧閉"          :bracket-open
   "括弧開"          :bracket-close
   "読点"            :comma
   "記号"            :symbol
   "文字"            :character
   "連体詞"          :adnominal
   "未知語"          :unknown-words
   "カタカナ文"      :katakana
   "漢文"            :chinese-writing
   "言いよどみ"      :hesitation
   "web誤脱"         :errors-omissions
   "方言"            :dialect
   "ローマ字文"      :latin-alphabet
   "新規未知語"      :new-unknown-words
   })

; see https://gist.github.com/masayu-a/3e11168f9330e2d83a68
(def keywordize-inflection
  {
   "ク語法"      :ku-wording
   "仮定形"      :conditional
   "一般"        :general
   "融合"        :integrated
   "命令形"      :imperative
   "已然形"      :realis
   "補助"        :auxiliary-inflection
   "意志推量形"  :volitional-tentative
   "未然形"      :irrealis
   "サ"          :sa
   "セ"          :se
   "撥音便"      :euphonic-change-n
   "終止形"      :conclusive
   "ウ音便"      :euphonic-change-u
   "促音便"      :euphonic-change-t
   "語幹"        :word-stem
   "連体形"      :attributive
   "イ音便"      :euphonic-change-i
   "省略"        :abbreviation
   "連用形"      :continuative
   "ト"          :change-to
   "ニ"          :change-ni
   "長音"        :long-sound
   "*"           :uninflected
   })

; see https://gist.github.com/masayu-a/b3ce862336e47736e84f
(def keywordize-inflection-type
  {"ユク"          :yuku
   "ダ行"          :da-column
   "ザ行変格"      :zahen-verb-irregular
   "ダ"            :da
   "タイ"          :tai
   "文語ラ行変格"  :classical-ra-column-change
   "ワ行"          :wa-column
   "コス"          :kosu
   "キ"            :ki
   "文語下二段"    :classical-shimonidan-verb-e-u-row
   "ス"            :su
   "ハ行"          :ha-column
   "上一段"        :kamiichidan-verb-i-row
   "イク"          :iku
   "マ行"          :ma-column
   "助動詞"        :auxiliary
   "シク"          :shiku
   "ナ行"          :na-column
   "ガ行"          :ga-column
   "ム"            :mu
   "ア行"          :a-column
   "ザンス"        :zansu
   "文語形容詞"    :classical-adjective
   "タ"            :ta
   "伝聞"          :reported-speech
   "ナイ"          :nai
   "ヘン"          :hen
   "文語助動詞"    :classical-auxiliary
   "ジ"            :ji
   "ワア行"        :wa-a-column
   "文語ナ行変格"  :classical-na-column-change
   "カ行変格"      :kahen-verb-irregular
   "ラシ"          :rashi
   "マイ"          :mai
   "タリ"          :tari
   "呉レル"        :kureru
   "形容詞"        :adjective
   "ゲナ"          :gena
   "一般+う"       :general-u
   "ザマス"        :zamasu
   "ゴトシ"        :gotoshi
   "ヌ"            :nu
   "文語上二段"    :classical-kaminidan-verb-u-i-row
   "ク"            :ku
   "サ行変格"      :sahen-verb-irregular
   "ラ行"          :ra-column
   "下一段"        :shimoichidan-verb-e-row
   "完了"          :final
   "ラシイ"        :rashii
   "文語四段"      :classical-yondan-verb
   "ドス"          :dosu
   "ザ行"          :za-column
   "ツ"            :shi
   "ヤス"          :yasu
   "バ行"          :ba-column
   "断定"          :assertive
   "ナンダ"        :nanda
   "ケリ"          :keri
   "文語サ行変格"  :classical-sa-column-change
   "タ行"          :ta-column
   "ケム"          :kemu
   "カ行"          :ka-column
   "ゲス"          :gesu
   "ヤ行"          :ya-column
   "マス"          :masu
   "レル"          :reru
   "サ行"          :sa-column
   "文語下一段"    :classical-shimoichidan-verb-e-row
   "ベシ"          :beshi
   "アル"          :aru
   "ヤ"            :ya
   "五段"          :godan-verb
   "一般"          :general
   "デス"          :desu
   "リ"            :ri
   "ナリ"          :nari
   "文語上一段"    :classical-kamiichidan-verb-i-row
   "無変化型"      :uninflected-form
   "ズ"            :zu
   "ジャ"          :ja
   "文語カ行変格"  :classical-ka-column-change
   "イウ"          :iu
   })


(defn split-dashes [s] (string/split s #"-"))

; Wrapper for all methods in [1] and [2]
; [1] UniDic-specific `Token` methods:
;     https://github.com/atilika/kuromoji/blob/master/kuromoji-unidic/src/main/java/com/atilika/kuromoji/unidic/Token.java
; [2] Parent `TokenBase` methods:
;     https://github.com/atilika/kuromoji/blob/master/kuromoji-core/src/main/java/com/atilika/kuromoji/TokenBase.java
(defn token-to-map [token]
  {:lemma (.getLemma token)
   :lemma-reading (.getLemmaReadingForm token)
   :lemma-pronunciation (.getPronunciationBaseForm token)
   :literal-pronunciation (.getPronunciation token)
   :part-of-speech (mapv #(or (get keywordize-pos %)
                              :unknown-pos)
                         (filter #(not (= % "*"))
                                 [(.getPartOfSpeechLevel1 token)
                                  (.getPartOfSpeechLevel2 token)
                                  (.getPartOfSpeechLevel3 token)
                                  (.getPartOfSpeechLevel4 token)]))
   :conjugation (mapv #(or (get keywordize-inflection %)
                           :unknown-inflection)
                      (split-dashes (.getConjugationForm token)))
   :conjugation-type (mapv #(or (get keywordize-inflection-type %)
                                :unknown-inflection-type)
                           (filter #(not (= % "*"))
                                   (split-dashes (.getConjugationType token))))
   :written-form (.getWrittenForm token)
   :written-base-form (.getWrittenBaseForm token)
   :language-type (.getLanguageType token)
   :initial-sound-alternation-type (.getInitialSoundAlterationType token)
   :initial-sound-alternation-form (.getInitialSoundAlterationForm token)
   :final-sound-alternation-type (.getFinalSoundAlterationType token)
   :final-sound-alternation-form (.getFinalSoundAlterationForm token)
   ; from TokenBase.java
   :literal (.getSurface token)
   :known? (.isKnown token)
   :user? (.isUser token)
   :position (.getPosition token)
   :all-features (string/split (.getAllFeatures token) #",")
   })

(def re-han #"\p{IsHan}")

(defn append-furigana [token-map]
    (merge token-map
           {:furigana
            (if (re-find re-han (:literal token-map))
              (furigana/kanji-reading->furigana
               (:lemma token-map)
               (kana/katakana->hiragana (:lemma-reading token-map)))
              nil)}))

(def unidic-tokenizer (Tokenizer.))

(defn parse
  [s]
  (mapv token-to-map
        (.tokenize unidic-tokenizer s)))
(defn parse-with-furigana
  [s]
  (mapv append-furigana (parse s)))

(defn parse-nbest
  [s n]
  (mapv #(mapv token-to-map
               %)
        (.multiTokenize unidic-tokenizer s n 1000000)))
(defn parse-nbest-with-furigana
  [s n]
  (mapv append-furigana (parse-nbest s n)))
