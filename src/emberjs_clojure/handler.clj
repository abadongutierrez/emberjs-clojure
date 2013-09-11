(ns emberjs-clojure.handler
	(:import com.mchange.v2.c3p0.ComboPooledDataSource)
	(:use compojure.core)
	(:use cheshire.core)
    (:use ring.util.response)
    (:use [hiccup.core :only (html h)]
          [hiccup.page :only (html5 include-css)])
    (:use ring.middleware.resource)
  	(:require
  		[compojure.handler :as handler]
    	[compojure.route :as route]
    	[ring.middleware.json :as middleware]
    	[ring.util.response :as ring]
    	[clojure.java.jdbc :as sql]))

(def db-config {
	:classname "org.h2.Driver"
    :subprotocol "h2"
    :subname "mem:documents"
    :user ""
    :password ""})

(defn pool [config]
	(let [cpds (doto (ComboPooledDataSource.)
    	(.setDriverClass (:classname config))
        (.setJdbcUrl (str "jdbc:" (:subprotocol config) ":" (:subname config)))
        (.setUser (:user config))
        (.setPassword (:password config))
        (.setMaxPoolSize 6)
        (.setMinPoolSize 1)
        (.setInitialPoolSize 1))]
        {:datasource cpds}))

(def pooled-db (delay (pool db-config)))

(defn db-connection [] @pooled-db)

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn get-all-documents []
	(response
    	(sql/with-connection (db-connection)
        	(sql/with-query-results results
            	["select * from documents"]
            	(into [] results)))))

(defn get-document [id]
	(sql/with-connection (db-connection)
    	(sql/with-query-results results
        	["select * from documents where id = ?" id]
          	(cond
            (empty? results) {:status 404}
            :else (response (first results))))))

(defn create-new-document [doc]
	(let [id (uuid)]
    	(sql/with-connection (db-connection)
        	(let [document (assoc doc "id" id)]
            (sql/insert-record :documents document)))
        (get-document id)))

(defn update-document [id doc]
	(sql/with-connection (db-connection)
		(let [document (assoc doc "id" id)]
		(sql/update-values :documents ["id=?" id] document)))
	(get-document id))

(defn delete-document [id]
	(sql/with-connection (db-connection)
		(sql/delete-rows :documents ["id=?" id]))
	{:status 204})

(defn common [title & body]
    (html5
        [:head
            [:meta {:charset "utf-8"}]
            [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
            [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1"}]
            [:title title]
            ;(include-css "/stylesheets/base.css"
            ;             "/stylesheets/skeleton.css"
            ;             "/stylesheets/screen.css")
            ;(include-css "http://fonts.googleapis.com/css?family=Sigmar+One&v1")
        	]
        [:body
            [:div {:id "header"}
                [:h1 {:class "container"} "Documents"]]
            [:div {:id "content" :class "container"} body]]))

(defn index []
    (common "Documents"
        [:div {:class "clear"}]
        [:div {:id "documents"}]
        ))

(defroutes app-routes
	(context "/" []
		(defroutes site-routes
			(GET "/" [] (ring/redirect "/index.html"))))
	(context "/documents" [] 
		(defroutes documents-routes
        	(GET  "/" [] (get-all-documents))
        	(POST "/" {body :body} (create-new-document body))
        	(context "/:id" [id] (defroutes document-routes
          		(GET    "/" [] (get-document id))
          		(PUT    "/" {body :body} (update-document id body))
    			(DELETE "/" [] (delete-document id))))))
	(route/not-found "Not Found"))

(def app
	(-> (handler/api app-routes)
  		(middleware/wrap-json-body)
    	(middleware/wrap-json-response)
    	(wrap-resource "public")))

(sql/with-connection (db-connection)
	;  (sql/drop-table :documents) ; no need to do that for in-memory databases
    (sql/create-table :documents 
    	[:id "varchar(256)" "primary key"]
    	[:title "varchar(1024)"]
        [:text :varchar]))
