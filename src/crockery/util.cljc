(ns crockery.util
  (:require [crockery.protocols :as p]))

(defn- pad-spaces [n]
  (apply str (repeat n " ")))

(defn align-cell [col s align]
  (let [width (:width col)
        s (str s)
        s (cond (<= (count s) width) s
                (:ellipsis col) (str (subs s 0 (- width 3)) "...")
                :else (subs s 0 width))
        len (count s)
        padding (- width len)]
    (case align
      :left (str s (pad-spaces padding))
      :right (str (pad-spaces padding) s)
      :center (let [half-padding (/ padding 2)]
                (str (pad-spaces (Math/floor half-padding))
                     s
                     (pad-spaces (Math/ceil half-padding)))))))

(defn column-defaults [{:keys [key-fn title title-align align render-cell render-title] :as col}]
  (let [nm (:name col)]
    (merge col
           {:align (keyword (or align :left))
            :key-fn (or key-fn #(get % nm))
            :render-cell (or render-cell p/render-cell*)
            :title (or title ((or render-title p/render-title*) (or nm key-fn)))
            :title-align (keyword (or title-align align :left))
            :when (:when col true)})))

(defn to-column-map [col]
  (if (map? col)
    col
    {:name col}))

(def column-xform (comp (map to-column-map)
                        (map column-defaults)))
