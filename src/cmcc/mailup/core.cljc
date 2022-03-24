(ns cmcc.mailup.core
  (:gen-class)
  (:import java.util.Base64)
  (:require
   [fierycod.holy-lambda.response :as hr]
   [fierycod.holy-lambda.agent :as agent]
   [fierycod.holy-lambda.core :as h]
   [ring.util.codec :refer [form-decode]]
   [clojure.walk :refer [keywordize-keys]]))

(def mailup-oauth2
  {:authorization-uri "https://services.mailup.com/Authorization/OAuth/LogOn"
   :access-token-uri "https://services.mailup.com/Authorization/OAuth/Token"
   :client-id ""
   :client-secret ""
   :access-query-param :access_token
   :scope []
   :grant-type "password"})

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
  (let [auth-req (oauth2/make-auth-request mailup-oauth2 "")
        ;; auth-resp is a keyword map of the query parameters added to the
        ;; redirect-uri by the authorization server
        ;; e.g. {:code "abc123"}
        access-token (oauth2/get-access-token mailup-oauth2 {} auth-req)
        result (oauth2/post (str "https://services.mailup.com/API/v1.1/Rest/ConsoleService.svc/Console/List/"
                                 14 "/Recipient")
                            {:oauth2 access-token})]

    ;; (println (str "Body: " (-> event :body base64decode form-decode keywordize-keys)))

    (println "Result: " result)

    ;; return a successful plain text response. See also, hr/json
    (hr/json {:message "Ok"})))

;; Specify the Lambda's entry point as a static main function when generating a class file
(h/entrypoint [#'MailupNewsletterSubcriptionWebhook])

;; Executes the body in a safe agent context for native configuration generation.
;; Useful when it's hard for agent payloads to cover all logic branches.
(agent/in-context
 (println "I will help in generation of native-configurations"))
