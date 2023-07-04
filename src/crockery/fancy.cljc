(ns crockery.fancy
  (:refer-clojure :exclude [double])
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

(deffixed rounded
  ["╭───┬───╮"
   "│ A │ B │"
   "├───┼───┤"
   "│ C │ D │"
   "╰───┴───╯"])

(deffixed rounded-grid
  ["╭───┬───╮"
   "│ A │ B │"
   "├───┼───┤"
   "│ C │ D │"
   "├───┼───┤"
   "│ E │ F │"
   "╰───┴───╯"])

(deffixed heavy
  ["┏━━━┳━━━┓"
   "┃ A ┃ B ┃"
   "┣━━━╋━━━┫"
   "┃ C ┃ D ┃"
   "┗━━━┻━━━┛"])

(deffixed heavy-grid
  ["┏━━━┳━━━┓"
   "┃ A ┃ B ┃"
   "┣━━━╋━━━┫"
   "┃ C ┃ D ┃"
   "┣━━━╋━━━┫"
   "┃ E ┃ F ┃"
   "┗━━━┻━━━┛"])

(deffixed double
  ["╔═══╦═══╗"
   "║ A ║ B ║"
   "╠═══╬═══╣"
   "║ C ║ D ║"
   "╚═══╩═══╝"])

(deffixed double-grid
  ["╔═══╦═══╗"
   "║ A ║ B ║"
   "╠═══╬═══╣"
   "║ C ║ D ║"
   "╠═══╬═══╣"
   "║ E ║ F ║"
   "╚═══╩═══╝"])

(deffixed mixed-grid
  ["┍━━━┯━━━┑"
   "│ A │ B │"
   "┝━━━┿━━━┥"
   "│ C │ D │"
   "├───┼───┤"
   "│ E │ F │"
   "┕━━━┷━━━┙"])
