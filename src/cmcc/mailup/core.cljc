(ns cmcc.mailup.core
  (:gen-class)
  (:import java.util.Base64)
  (:require
   [fierycod.holy-lambda.response :as hr]
   [fierycod.holy-lambda.agent :as agent]
   [fierycod.holy-lambda.core :as h]
   [ring.util.codec :refer [form-decode]]
   [clojure.walk :refer keywordize-keys]))

;; optionally, provide runtime specific implementations if needed
(defn say-hello
  []
  #?(:bb  (str "Hello world! Babashka is a sweet friend of mine! Babashka version: " (System/getProperty "babashka.version"))
     :clj "Hello world"))

(defn base64decode [to-decode]
  (String. (.decode (Base64/getDecoder) to-decode)))

(defn MailupNewsletterSubcriptionWebhook
  "I can run on Java, Babashka or Native runtime..."
  [{:keys [event ctx] :as request}]

  (println (str "Body: " (-> event :body base64decode form-decode keywordize-keys)))

  ;; return a successful plain text response. See also, hr/json
  (hr/json {:message "Ok"}))

;; Specify the Lambda's entry point as a static main function when generating a class file
(h/entrypoint [#'MailupNewsletterSubcriptionWebhook])

;; Executes the body in a safe agent context for native configuration generation.
;; Useful when it's hard for agent payloads to cover all logic branches.
(agent/in-context
 (println "I will help in generation of native-configurations"))
