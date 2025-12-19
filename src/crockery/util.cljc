(ns crockery.util
  (:require
   [crockery.protocols :as p]
   [crockery.strings :as strings]
   [clojure.string :as str]))

(defn- pad-spaces [n]
  (apply str (repeat n " ")))

(defn align-cell [col s align]
  (let [width (:width col)
        string (str s)
        plain (if (:ignore-ansi? col)
                (strings/strip-ansi string)
                string)
        len (count plain)
        [s len] (cond (<= len width) [string len]
                      (:ellipsis col) [(str (subs plain 0 (max 0 (- width 3))) "...") width]
                      :else [(subs plain 0 width) width])
        padding (- width len)]
    (case align
      :decimal (let [[a b] (str/split s #"\." 2)
                     d (if b (str "." b) "")
                     pre (get-in col [:decimal-info :pre] 0)]
                 (str (pad-spaces (- pre (count a)))
                      a
                      d
                      (pad-spaces (- width pre (count d)))))
      :left (str s (pad-spaces padding))
      :right (str (pad-spaces padding) s)
      :center (let [half-padding (/ padding 2)]
                (str (pad-spaces (Math/floor half-padding))
                     s
                     (pad-spaces (Math/ceil half-padding)))))))


(defn- non-string-seq? [coll]
  (and (seqable? coll) (not (string? coll))))

(defn data->cols-rows [data]
  (cond
    (map? data)
    [[:key :value]
     (map (fn [[k v]] {:key k :value v}) data)]

    (non-string-seq? data)
    (let [f (first data)]
      (cond
        (map? f)
        [(keys f)
         data]

        (non-string-seq? f)
        [(map-indexed (fn [i h] {:name i :title h}) f)
         (rest data)]

        :else
        [[:value]
         (map (fn [v] {:value v}) data)]))

    :else
    [[:value]
     [{:value data}]]))

(defn normalize-column [{:keys [key-fn title title-align align render-title render-cell] :as col}]
  (let [nm (:name col)
        align (keyword (or align :left))]
    (merge col
           {:align align
            :key-fn (or key-fn #(get % nm))
            :render-cell (or render-cell p/render-cell*)
            :title (or title ((or render-title p/render-title*) (or nm key-fn)))
            :title-align (keyword (or title-align (#{:right :left :center} align) :left))
            :when (:when col true)})))

(defn to-column-map [col]
  (if (map? col)
    col
    {:name col}))
