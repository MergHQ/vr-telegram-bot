(ns vr_telegram_bot.templates.route-list
  (:require [clj-time.format :as f]))

(def basic-formatter (f/formatter :date-time))
(def time-parser (f/formatter "HH:mm"))

(defn- map-to-string [{:keys [departure arrival legs offer]}]
  (str "*"
       (f/unparse time-parser (f/parse basic-formatter (:time departure)))
       " -> "
       (f/unparse time-parser (f/parse basic-formatter (:time arrival)))
       "*"
       (when (< (count legs) 2) " SOURA ")
       " - "
       (if (nil? (:price offer))
         "Loppuunmyyty"
         (str (Math/round (double (/ (:price offer) 100))) "€"))))

(defn template [items]
  (if (> (count items) 0)
    (let [from (get-in (first items) [:departure :station])
          to   (get-in (first items) [:arrival :station])]
      (str from " -> " to "\n"
           (reduce
            (fn [value row]
              (str value "\n" row))
            (map map-to-string items))))
    "Not found"))
