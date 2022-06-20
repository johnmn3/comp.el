(ns todomvc.views.comps
  (:require
   [re-frame.core :refer [dispatch]]
   [comp.el :as comp]
   [todomvc.views.affects :as a]
   [todomvc.views.styled :as styled]))

(defn unchecked [props]
  [:span (merge props styled/circle)])

(defn checked [props]
  [:span (styled/deep-merge props styled/checked-circle styled/check)
   [:div styled/check-leg]
   [:div styled/check-foot]])

(defn complete-check [{:keys [id done]}]
  (let [toggle #(dispatch [:toggle-done id])]
    (if done
      [checked {:on-click toggle}]
      [unchecked {:on-click toggle}])))

(def delete-todo
  (comp/div
   {:as ::delete-todo :with a/void-todo
    :props styled/delete-todo
    :on-click #(dispatch [:delete-todo (:id (:todo %))])
    :children ["×"]}))

(def todo-display
  (comp/label
   {:as ::todo-display :with a/void-todo
    :props styled/todo-display
    :props/ef (fn [{:as todo :keys [editing]}]
                (merge {:on-double-click #(reset! editing true)}
                       (when (:done todo)
                         styled/todo-done)))
    :children (fn [{{title :title} :props}]
                [title])}))

(def todo-header-title
  (comp/box
   {:as ::todo-header-title
    :props styled/todo-header-title}))

(def filter-anchor
  (comp/a
   {:as ::a :with [a/selected?]
    :props (merge styled/filter-anchor
                  {:on-selected #(update % :style
                                         assoc :border-color
                                         "rgba(175, 47, 47, 0.2)")})}))

(def filter-all
  (filter-anchor
   {:as :all :with a/void-todo
    :children ["All"]}))

(def filter-active
  (filter-anchor
   {:as :active :with a/void-todo
    :children ["Active"]}))

(def filter-done
  (filter-anchor
   {:as :done :with a/void-todo
    :children ["Completed"]}))
