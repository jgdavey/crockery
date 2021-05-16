(ns crockery.protocols
  (:require [crockery.strings :refer [titleize]])
  #?(:clj
     (:import [java.text SimpleDateFormat])))

#?(:clj
   (set! *warn-on-reflection* true))

(defprotocol RenderTable
  (render-table [_ cols rows]))

(defprotocol RenderCell
  (render-cell [_ _opts]))

(defprotocol RenderHeader
  (render-header [_]))

#?(:clj
   ;; See clojure instant
   (def ^:private ^ThreadLocal thread-local-utc-date-format
    ;; SimpleDateFormat is not thread-safe, so we use a ThreadLocal proxy for access.
    ;; http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4228335
     (proxy [ThreadLocal] []
       (initialValue []
         ;; javascript style
         (doto (SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ss'Z'")
           (.setTimeZone (java.util.TimeZone/getTimeZone "GMT")))))))

#?(:clj
   (defn format-date ^String [^java.util.Date d]
     (let [^java.text.DateFormat utc-format (.get thread-local-utc-date-format)]
       (.format utc-format d))))

#?(:cljs
   (defn format-date [d]
     (.toJSON d)))

(extend-protocol RenderCell
  #?(:clj java.util.Date
     :cljs js/Date)
  (render-cell [this _] (format-date this))

  #?(:clj String
     :cljs string)
  (render-cell [this _] this)

  #?(:clj Object
     :cljs default)
  (render-cell [this _] (str this))

  nil
  (render-cell [this _] ""))

(extend-protocol RenderHeader
  #?(:clj java.util.Date
     :cljs js/Date)
  (render-header [this] (format-date this))

  #?(:clj clojure.lang.Named
     :cljs Keyword)
  (render-header [this] (titleize (name this)))

  #?(:clj String
     :cljs string)
  (render-header [this] this)

  #?(:clj Object
     :cljs default)
  (render-header [this] (str this))

  nil
  (render-header [this] ""))

#?(:cljs
   (extend-type Symbol
     RenderHeader
     (render-header [this] (titleize (name this)))))

(defn render-cell* [value]
  (render-cell value nil))

(defn render-title* [value]
  (render-header value))
