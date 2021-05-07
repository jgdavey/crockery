(ns crockery.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [crockery.core :as crock]
            [crockery.protocols :as p]
            [clojure.spec.test.alpha :as stest]))

(s/def ::when boolean?)
(s/def ::key-fn (s/or :kw keyword?
                      :fn (s/fspec :args (s/cat :row map?))))
(s/def ::title string?)
(s/def ::title-align #{:left :center :right})
(s/def ::align #{:left :center :right})
(s/def ::ellipsis boolean?)

(s/def ::colspec (s/keys :req-un [::key-fn
                                  ::title
                                  ::title-align
                                  ::align
                                  ::when]
                         :opt-un [::ellipsis]))

(s/def ::name keyword?)
(s/def ::col-arg-map (s/keys :req-un [(or ::name ::key-fn)]
                             :opt-un [::name
                                      ::key-fn
                                      ::title
                                      ::align
                                      ::title-align
                                      ::when
                                      ::ellipsis]))
(s/def ::col-arg (s/or :kw keyword?
                       :map ::col-arg-map))

(s/def ::renderer (s/with-gen
                    #(satisfies? p/RenderTable %)
                    #(gen/return
                      (reify p/RenderTable
                       (-render-table [_ _ _] [""])))))

(s/def ::format (s/or :built-in #{:org :fancy :gfm :tsv}
                      :custom ::renderer))

(s/def ::opts-arg (s/nilable (s/keys :opt-un [::format])))
(s/def ::cols-arg (s/nilable (s/coll-of ::col-arg)))

(def data-gen (s/gen (s/coll-of (s/map-of keyword? any?) :into [])))

(s/def ::data-arg (s/nilable
                   (s/with-gen
                     any?
                     (constantly data-gen))))

(s/def ::table-args
  (s/alt
   :data (s/cat :data ::data-arg)
   :cols-or-opts+data (s/cat :cols-or-opts (s/or :cols ::cols-arg
                                                 :opts ::opts-arg)
                             :data ::data-arg)
   :opts-cols-data (s/cat :opts ::opts-arg
                          :cols ::cols-arg
                          :data ::data-arg)))

(s/fdef crock/table
  :args ::table-args
  :ret (s/every string?))

(comment
  (stest/instrument `crock/table)
  )
