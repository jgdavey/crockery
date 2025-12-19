(ns crockery.strings
  (:require [clojure.string :as str]
            [lambdaisland.ansi :as ansi]))

(defn string #?(:clj {:tag String})
  [elem]
  (str (cond
         (keyword? elem) (name elem)
         (symbol? elem) (name elem)
         :else elem)))

(defn- title-case-word [w]
  (if (zero? (count w))
    w
    (str (.toUpperCase (subs w 0 1))
         (subs w 1))))

(defn title-case [s]
  (str/join " " (map title-case-word (str/split s #"\s"))))

(defn titleize #?(:clj {:tag String}) [n]
  (title-case
   (.replaceAll (string n) "-" " ")))

(def ^:private ^:const escape-map
  {\newline "\\n"
   \tab  "\\t"
   \return "\\r"
   \formfeed "\\f"
   \backspace "\\b"})

(defn escape [s]
  (str/escape s escape-map))

(defn strip-ansi [s]
  (->> s
       (ansi/token-stream)
       (filter string?)
       (apply str)))
