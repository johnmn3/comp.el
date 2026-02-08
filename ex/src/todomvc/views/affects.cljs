(ns todomvc.views.affects
  (:require
   [comp.el :as comp]
   [comp.props :refer [void props]]))

(def void-todo
  (comp/derive void
   {:as ::void-todo
    :props/void [:id :editing :on-save :on-stop :title :done :selected? :new?]}))

(def selected?
  (comp/derive props
   {:as ::selected?
    :props/void [:selected? :on-selected]
    :props/ef (fn [{:as props :keys [on-selected selected?]}
                   {:keys [is]}]
                (-> (merge {:href (str "#/" (some-> is name))}
                           (when (= is selected?)
                             ((or on-selected identity) props)))))}))
