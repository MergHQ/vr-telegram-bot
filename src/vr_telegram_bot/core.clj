(ns vr-telegram-bot.core
  (:require [clojure.core.async :refer [<!!]]
            [clojure.string :as str]
            [environ.core :refer [env]]
            [morse.handlers :as h]
            [morse.api :as t]
            [morse.polling :as p]
            [vr_telegram_bot.service.vr-service :as vr-service]
            [vr_telegram_bot.templates.route-list :as route-list]
            [clojure.string :refer [split]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-body]]
            [compojure.core :as c])
  (:gen-class))

(def token (env :telegram-token))
(def environment (or (env :environment) "dev"))
(def port (Integer/parseInt (or (env :port) "3000")))
(def passenger-map
  {:O "STUDENT"
   :A "ADULT"
   :L "CHILD"})

(defn parse-message [text]
  (let [args (->> (split text #" ")
                  (drop 1))]
    {:from      (nth args 0)
     :to        (nth args 1)
     :passenger (nth args 2)}))

(h/defhandler handler
  (h/command-fn "etsi"
                (fn [msg]
                  (when (not-empty (re-matches #"/etsi [A-Ö]{2,3} [A-Ö]{2,3} [A-Ö]{1}" (:text msg)))
                    (let [params  (parse-message (:text msg))
                          items   (vr-service/get-routes
                                   (:from params)
                                   (:to params)
                                   (passenger-map (keyword (:passenger params))))
                          text    (route-list/template items)
                          chat-id (-> msg :chat :id)]
                      (t/send-text token chat-id {:parse_mode "Markdown"} text))))))

(c/defroutes app
  (c/POST "/handler"
          {updates :body}
          (if (list? updates)
            (map handler updates)
            (handler updates))))

(defn -main
  [& args]
  (when (str/blank? token)
    (println "Please provde token in TELEGRAM_TOKEN environment variable!")
    (System/exit 1))
  (println "Starting the vr-telegram-bot")
  (if (= environment "prod")
    (do
      (t/set-webhook token (env :telegram-handler))
      (jetty/run-jetty (wrap-json-body app {:keywords? true}) {:port port}))
    (<!! (p/start token handler))))
