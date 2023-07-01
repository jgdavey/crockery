(ns crockery.simple
  (:require [crockery.fixed :refer [make-renderer
                                    parse-format]]))

(def plain
  (make-renderer {:chrome (parse-format ["A  B"
                                         "C  D"
                                         ])}))

(def simple
  (make-renderer {:chrome (parse-format ["A  B"
                                         "-  -"
                                         "C  D"])}))

(def presto
  (make-renderer {:chrome (parse-format [" A | B "
                                         "---|---"
                                         " C | D "])}))

(def grid
  (make-renderer {:chrome (parse-format ["+---+---+"
                                         "| A | B |"
                                         "+===+===+"
                                         "| C | D |"
                                         "+---+---+"
                                         "| E | F |"
                                         "+---+---+"])}))
