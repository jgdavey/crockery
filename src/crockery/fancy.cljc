(ns crockery.fancy
  (:require [crockery.fixed :refer [make-renderer
                                    parse-format]]))

(def fancy
  (make-renderer {:chrome (parse-format ["┌───┬───┐"
                                         "│ A │ B │"
                                         "├───┼───┤"
                                         "│ C │ D │"
                                         "└───┴───┘"])}))

(def fancy-grid
  (make-renderer {:chrome (parse-format ["┌───┬───┐"
                                         "│ A │ B │"
                                         "├───┼───┤"
                                         "│ C │ D │"
                                         "├───┼───┤"
                                         "│ E │ F │"
                                         "└───┴───┘"])}))
