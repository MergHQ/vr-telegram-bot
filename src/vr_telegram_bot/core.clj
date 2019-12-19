(ns vr-telegram-bot.core
  (:require [clojure.core.async :refer [<!!]]
            [clojure.string :as str]
            [environ.core :refer [env]]
            [morse.handlers :as h]
            [morse.api :as t]
            [morse.polling :as p]
            [vr_telegram_bot.service.vr-service :as vr-service]
            [vr_telegram_bot.templates.route-list :as route-list])
  (:gen-class))

(def token (env :telegram-token))
(def environment (or (env :environment) "dev"))

(h/defhandler handler
  (h/command-fn "etsi"
                (fn [msg]
                  (println)
                  (let [items (vr-service/get-routes "HKI" "TPE" "ADULT")
                        text  (route-list/template items)]
                    (t/send-text token (get-in msg [:chat :id]) {:parse_mode "Markdown"} text)))))

(defn -main
  [& args]
  (when (str/blank? token)
    (println "Please provde token in TELEGRAM_TOKEN environment variable!")
    (System/exit 1))
  (println "Starting the vr-telegram-bot")
  (<!! (p/start token handler)))
