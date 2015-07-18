(defproject shorty "0.1.0-SNAPSHOT"
  :description "URL shortener thing"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.1"]
                 [http-kit "2.1.16"]
                 [ring/ring-defaults "0.1.2"]
                 [commons-validator/commons-validator "1.4.1"]
                 [org.clojure/tools.logging "0.3.1"]
                 [environ "1.0.0"]
                 [factual/clj-leveldb "0.1.1"]]

  :plugins [[lein-ring "0.8.13"]
            [lein-environ "1.0.0"]]
  :ring {:handler shorty.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]
                        [midje "1.7.0-beta1"]
                        [criterium "0.4.3"]
                        [org.clojure/test.check "0.7.0"]]
         :env {:dbname "/tmp/shorty.db"}}})
