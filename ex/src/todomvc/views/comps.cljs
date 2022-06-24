(ns todomvc.views.comps
  (:require
   [re-frame.core :refer [dispatch]]
   [clojure.string :as str]
   [reagent.core :as r]
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
    :on-click #(dispatch [:delete-todo (:is %)])
    :children ["×"]}))

(def todo-display
  (comp/label
   {:as ::todo-display :with a/void-todo
    :props styled/todo-display
    :props/ef (fn [{:as todo :keys [editing]}]
                (merge {:on-double-click #(reset! editing true)}
                       (when (:done todo)
                         styled/todo-done)))}))

(def todo-input
  (comp/raw-input
   {:as ::todo-input :with [a/void-todo]
    :props styled/todo-input
    :props/void [:af-state]
    :props/ef (fn [{:as props :keys [on-save on-stop af-state]}]
                (let [stop #(do (reset! af-state "")
                                (when on-stop (on-stop)))
                      save #(do (on-save (some-> af-state deref str str/trim))
                                (stop))]
                  {:auto-focus  true
                   :on-blur     save
                   :value       (some-> af-state deref)
                   :on-change   (fn [ev] (reset! af-state (-> ev .-target .-value)))
                   :on-key-down #(case (.-which %)
                                   13 (save)
                                   27 (stop)
                                   nil)}))}))

(def new-todo
  (todo-input
   {:as ::new-todo
    :props (merge styled/new-todo
                  {:placeholder "What needs to be done?"
                   :af-state (r/atom nil)
                   :on-save #(when (seq %)
                               (dispatch [:add-todo %]))})}))

(def existing-todo
  (todo-input
   {:as ::existing-todo
    :props styled/edit-todo
    :props/af (fn [{:keys [editing]
                    {:keys [id title]} :todo}]
                {:af-state (r/atom title)
                 :on-save #(if (seq %)
                             (dispatch [:save id %])
                             (dispatch [:delete-todo id]))
                 :on-stop #(reset! editing false)})}))

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
