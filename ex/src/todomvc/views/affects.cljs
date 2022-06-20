(ns todomvc.views.affects
  (:require
   [comp.props :refer [void props]]))

(def void-todo
  (void
   {:as ::void-todo
    :props/void [:id :on-save :on-stop :title :done :selected?]}))

(def selected?
  (props
   {:as ::selected?
    :props/void [:selected? :on-selected]
    :props/ef (fn [{:as props :keys [on-selected selected?]}
                   {:keys [is]}]
                (-> (merge {:href (str "#/" (some-> is name))}
                           (when (= is selected?)
                             ((or on-selected identity) props)))))}))
