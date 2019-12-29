(defproject vr-telegram-bot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [environ             "1.1.0"]
                 [morse               "0.2.4"]
                 [clj-http            "3.10.0"]
                 [cheshire            "5.9.0"]
                 [clj-time            "0.15.2"]
                 [ring                "1.8.0"]
                 [compojure           "1.6.1"]]

  :plugins [[lein-environ "1.1.0"]]

  :main ^:skip-aot vr-telegram-bot.core
  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}})
