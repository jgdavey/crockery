(ns crockery.fancy
  (:require [crockery.fixed :refer [make-renderer
                                    parse-format]]))

(def renderer (make-renderer {:chrome (parse-format ["┌───┬───┐"
                                                     "│ A │ B │"
                                                     "├───┼───┤"
                                                     "│ C │ D │"
                                                     "└───┴───┘"])}))

