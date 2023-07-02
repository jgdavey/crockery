(ns crockery.fancy
  (:require
   [crockery.fixed #?(:cljs :refer-macros
                      :clj :refer) [deffixed]]))

(deffixed fancy
  ["┌───┬───┐"
   "│ A │ B │"
   "├───┼───┤"
   "│ C │ D │"
   "└───┴───┘"])

(deffixed fancy-grid
  ["┌───┬───┐"
   "│ A │ B │"
   "├───┼───┤"
   "│ C │ D │"
   "├───┼───┤"
   "│ E │ F │"
   "└───┴───┘"])
