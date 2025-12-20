(ns crockery.simple
  (:require
   [crockery.fixed #?(:cljs :refer-macros
                      :clj :refer) [deffixed]]
   [clojure.string :as str]))

(deffixed plain
  ["A  B"
   "C  D"])

(deffixed simple
  ["A  B"
   "-  -"
   "C  D"])

(deffixed presto
  [" A | B "
   "---|---"
   " C | D "])

(deffixed grid
  ["+---+---+"
   "| A | B |"
   "+===+===+"
   "| C | D |"
   "+---+---+"
   "| E | F |"
   "+---+---+"])

(deffixed rst
  ["=  ="
   "A  B"
   "=  ="
   "C  D"
   "=  ="])

(deffixed org
  ["|---+---|"
   "| A | B |"
   "|---+---|"
   "| C | D |"
   "|---+---|"]
  :escape #(str/escape % {\| "\\vert{}"}))
