(ns build
  (:require [clojure.tools.build.api :as b]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [deps-deploy.deps-deploy :as dd]))

(def lib 'com.joshuadavey/crockery)
(def version (str/trim (slurp (io/file "VERSION"))))
(def target-dir "target")
(def class-dir "target/classes")

(defn- jar-opts [opts]
  (assoc opts
          :lib lib :version version
          :jar-file (format "target/%s-%s.jar" lib version)
          :scm {:tag (str "v" version)}
          :basis (b/create-basis {})
          :class-dir class-dir
          :target target-dir
          :src-dirs ["src"]))

(defn clean [_]
  (b/delete {:path target-dir}))

(defn build-jar
  [opts]
  (let [opts (jar-opts opts)]
    (clean opts)
    (println "* Writing pom.xml")
    (b/write-pom opts)
    (println "* Copying source")
    (b/copy-dir {:src-dirs ["resources" "src"] :target-dir class-dir})
    (println (str "* Building JAR at " (:jar-file opts)))
    (b/jar opts)))

(defn install
  "Install the JAR locally."
  [opts]
  (let [opts (jar-opts opts)]
    (b/install opts))
  opts)

(defn deploy
  "Deploy the JAR to Clojars."
  [opts]
  (let [{:keys [jar-file] :as opts} (jar-opts opts)]
    (when-not (.exists (io/file jar-file))
      (println "* Running build-jar")
      (build-jar opts))
    (println "* Deploying to Clojars")
    (dd/deploy {:installer :remote :artifact (b/resolve-path jar-file)
                :pom-file (b/pom-path (select-keys opts [:lib :class-dir]))}))
  opts)
