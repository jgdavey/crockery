(ns crockery.protocols
  (:require [crockery.strings :refer [titleize]]))

#?(:clj
   (set! *warn-on-reflection* true))

(defprotocol RenderTable
  (render-table [_ cols rows]))

(defprotocol RenderCell
  (render-cell [_ _opts]))

(defprotocol RenderHeader
  (render-header [_]))

#?(:clj
   (defn format-date ^String [^java.util.Date d]
     (str (.toInstant d))))

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
