(defproject emberjs-clojure "0.0.1"
    :description "FIXME: write description"
    :url "http://github.com/abadongutierrez/emberjs-clojure"
    :dependencies [
        [org.clojure/clojure "1.5.1"]
        [compojure "1.1.5"]
        [ring/ring-json "0.1.2"]
        [c3p0/c3p0 "0.9.1.2"]
        [org.clojure/java.jdbc "0.2.3"]
        [com.h2database/h2 "1.3.168"]
        [cheshire "4.0.3"]
        [hiccup "1.0.2"]
    ]
    :plugins [
        [lein-ring "0.8.5"]]
    :ring {:handler emberjs-clojure.handler/app}
    :profiles
    {:dev {:dependencies [[ring-mock "0.1.5"]]}})
