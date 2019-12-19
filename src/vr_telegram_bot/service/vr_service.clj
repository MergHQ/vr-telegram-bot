(ns vr_telegram_bot.service.vr-service
  (:require [clj-http.client :refer [post]]
            [cheshire.core :refer [encode parse-string]]))

(def url "https://backend-v3-prod.vrfi.prodvrfi.vrpublic.fi/")
(def query
  "query getConnections($outbound: ConnectionInput!, $passengers: [PassengerInput!]!) {\n  connections(input: $outbound) {\n    ... on NoConnections {\n      noConnectionsReason\n      __typename\n    }\n    ... on ConnectionList {\n      items {\n        id\n        duration\n        transferCount\n        departure {\n          station\n          time\n          __typename\n        }\n        services\n        arrival {\n          station\n          time\n          __typename\n        }\n        legs {\n          id\n          services\n          departure {\n            station\n            time\n            __typename\n          }\n          arrival {\n            station\n            time\n            __typename\n          }\n          duration\n          train {\n            id\n            type\n            label\n            __typename\n          }\n          __typename\n        }\n        offer(passengers: $passengers) {\n          ... on Offer {\n            id\n            price\n            __typename\n          }\n          ... on NoOffer {\n            noOfferReason\n            __typename\n          }\n          __typename\n        }\n        __typename\n      }\n      __typename\n    }\n    __typename\n  }\n}\n")

(defn create-query-string [from to passenger-type]
  {:operationName "getConnections"
   :variables     {:outbound
                   {:departure            from
                    :arrival              to
                    :dateTime             "2019-12-19"
                    :showDepartedJourneys false}
                   :passengers [{:type passenger-type}]}
   :query         query})

(defn post-query [body]
  (post url
        {:body         (encode body)
         :content-type :json
         :accept       :json}))

(defn parse-response [response]
  (let [body  (parse-string (:body response) true)
        items (get-in body [:data :connections :items])]
    items))

(defn get-routes [from to passenger-type]
  (-> (create-query-string from to passenger-type)
      (post-query)
      (parse-response)))
