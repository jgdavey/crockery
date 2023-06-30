(ns crockery.org
  (:require
   [clojure.string :as str]
   [crockery.fixed :refer [make-renderer
                           parse-format]]))

(defn org-escape [^String s]
  (str/escape s {\| "\\vert{}"}))

(def renderer (make-renderer {:chrome (parse-format ["|---+---|"
                                                     "| A | B |"
                                                     "|---+---|"
                                                     "| C | D |"
                                                     "|---+---|"])
                              :escape org-escape}))
