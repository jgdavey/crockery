(ns crockery.fixed
  (:require [crockery.protocols :as p]
            [crockery.strings :as strings]
            [crockery.util :refer [align-cell]]
            #?(:clj [crockery.terminal :as term])))

(defn maximum
  ([] 0)
  ([x] x)
  ([a b]
      (cond
        (nil? b) a
        (> b a) b
        :else a)))

(defn calculate-width [rows]
  (transduce (map count) maximum 0 rows))

(defn columns-with-widths [cols pre-rendered]
  (map-indexed
   (fn [i {:keys [width] :as col}]
     (assoc col
            :user-width width
            :width (or width
                       (calculate-width (map #(nth % i) pre-rendered)))))
   cols))

(defn rebalance-widths [colspecs chromeless-max-width]
  (if (<= (apply + (map :width colspecs)) chromeless-max-width)
    colspecs
    (let [resize-user-widths (> (apply + (map #(or (:user-width %) 3) colspecs))
                                chromeless-max-width)]
      (loop [out []
             remaining colspecs
             remaining-width chromeless-max-width]
        (if (empty? remaining)
          out
          (let [colspec (first remaining)
                per-col (quot remaining-width (count remaining))
                new-col (if (and (> (:width colspec) per-col)
                                 (or resize-user-widths (nil? (:user-width colspec))))
                          (assoc colspec
                                 :width per-col
                                 :ellipsis true)
                          colspec)]
            (recur
             (conj out new-col)
             (next remaining)
             (- remaining-width (:width new-col)))))))))

(defrecord FixedWidthRender [th td assemble escape chrome-width-fn]
  p/RenderTable
  (render-table [_ opts cols data]
    (let [cell-fns (into []
                         (map (fn [col]
                                (let [key-fn (:key-fn col)
                                      render-cell (:render-cell col)]
                                  #(-> % key-fn render-cell escape))))
                         cols)
          chrome-width (chrome-width-fn (count cols))
          max-width (or (:max-width opts)
                        #?(:clj (term/detect-terminal-width))
                        #_200) ;; TODO should this default?
          rendered-headers (for [col cols]
                             (-> col :title escape))
          rendered-rows (for [row data]
                          (for [cell-fn cell-fns]
                            (cell-fn row)))
          colspecs (columns-with-widths cols (cons rendered-headers rendered-rows))
          colspecs (if max-width
                     (rebalance-widths colspecs (- max-width chrome-width))
                     colspecs)]
      (assemble colspecs
                (map th colspecs rendered-headers)
                (for [row rendered-rows]
                  (map td colspecs row))))))

(defn aligned-th [col s]
  (align-cell col
              s
              (:title-align col)))

(defn aligned-td [col s]
  (align-cell col
              s
              (:align col)))

(defn make-renderer [{:keys [td th assemble escape chrome-width-fn]}]
  (map->FixedWidthRender {:td (or td (fn [_ s] s))
                          :th (or th (fn [_ s] s))
                          :escape (comp strings/escape (or escape identity))
                          :assemble assemble
                          :chrome-width-fn (or chrome-width-fn (fn [i] (+ 4 (* 3 (dec i)))))}))
