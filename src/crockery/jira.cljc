(ns crockery.jira
  (:require
   [clojure.string :as str]
   [crockery.fixed #?(:cljs :refer-macros
                      :clj :refer) [deffixed]]))

(defn jira-header [_colspecs [h & body]]
  (cons (str/replace h "|" "||") body))

(defn escape-pipe [^String s]
  (str/escape s {\| "\\|"}))

(deffixed jira
  ["| A | B |"
   "| C | D |"]
  :postprocess jira-header
  :escape escape-pipe)
