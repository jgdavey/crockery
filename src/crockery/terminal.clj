(ns crockery.terminal
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]]))

(defn- positive-int [s]
  (when (not-empty s)
    (let [n (Integer. (str/trim s))]
      (when (pos-int? n) n))))

(defn- get-paths []
  (-> (System/getenv "PATH") (str/split #":")))

(defn- command-exists? [cmd paths]
  (some
   #(-> (str % "/" cmd) io/file .isFile)
   paths))

(defn- stty-detect [paths]
  (when (command-exists? "stty" paths)
    (-> (sh "/bin/sh" "-c" "stty size < /dev/tty")
        :out
        (str/split #" +")
        last
        positive-int)))

(defn- tput-detect [paths]
  (when (command-exists? "tput" paths)
    (-> (sh "/bin/sh" "-c" "tput cols 2> /dev/tty")
        :out
        positive-int)))

(defn detect-terminal-width []
  (let [paths (get-paths)]
    (or
     (some-> (System/getenv "COLUMNS") positive-int)
     (stty-detect paths)
     (tput-detect paths))))
