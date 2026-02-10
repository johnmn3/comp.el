(ns todomvc.views.affects
  (:require
   [comp.props :refer [void props]]))

(def void-todo
  (-> void
      (update :id conj ::void-todo)
      (update :props/void #(into (or % []) [:id :editing :on-save :on-stop :title :done :selected? :new?]))))

(def selected?
  (-> props
      (update :id conj ::selected?)
      (update :props/void #(into (or % []) [:selected? :on-selected]))
      (update :props/tf conj
              ::selected?
              (fn [{:as props :keys [on-selected selected?]}
                   {:keys [is]}]
                (-> (merge {:href (str "#/" (some-> is name))}
                           (when (= is selected?)
                             ((or on-selected identity) props))))))))
