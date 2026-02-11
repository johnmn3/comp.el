(ns todomvc.views.comps
  (:require
   [re-frame.core :refer [dispatch]]
   [clojure.string :as str]
   [reagent.core :as r]
   [comp.el :as comp]
   [comp.props :as p]
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
  (-> comp/div
      (update :id conj ::delete-todo)
      (update :with conj styled/delete-todo a/void-todo)
      (assoc :on-click #(dispatch [:delete-todo (:is %)]))))

(def todo-display
  (-> comp/label
      (update :id conj ::todo-display)
      (update :with conj styled/todo-display a/void-todo)
      (update :props/tf conj
              ::todo-display
              (fn [{:as todo :keys [editing]}]
                (merge {:on-double-click #(reset! editing true)}
                       (when (:done todo)
                         styled/todo-done))))))

(def todo-input
  (-> comp/raw-input
      (update :id conj ::todo-input)
      (update :with conj styled/todo-input a/void-todo)
      (update :props/void #(into (or % []) [:af-state]))
      (update :props/tf conj
              ::todo-input
              (fn [{:keys [on-save on-stop af-state]}]
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
                                   nil)})))))

(def new-todo
  (-> todo-input
      (update :id conj ::new-todo)
      (update :with conj styled/new-todo)
      (update :props p/merge-with-styles
              {:placeholder "What needs to be done?"
               :af-state (r/atom nil)
               :on-save #(when (seq %)
                           (dispatch [:add-todo %]))})))

(def existing-todo
  (-> todo-input
      (update :id conj ::existing-todo)
      (update :with conj styled/edit-todo)
      (update :props/tf-pre conj
              ::existing-todo
              (fn [{:keys [editing af-state]
                    {:keys [id title]} :todo}]
                ;; Use provided af-state if available (persistent atom from form-2
                ;; outer let), falling back to creating a new one. Initialize
                ;; the persistent atom to title when entering edit mode.
                (let [state (or af-state (r/atom title))]
                  (when (nil? @state)
                    (reset! state title))
                  {:af-state state
                   :on-save #(if (seq %)
                               (dispatch [:save id %])
                               (dispatch [:delete-todo id]))
                   :on-stop #(do (reset! state nil)
                                 (reset! editing false))})))))

(def todo-header-title
  (-> comp/box
      (update :id conj ::todo-header-title)
      (update :with conj styled/todo-header-title)))

(def filter-anchor
  (-> comp/a
      (update :id conj ::filter-anchor)
      (update :with conj styled/filter-anchor a/selected?)
      (assoc-in [:props :on-selected]
                #(update % :style assoc :border-color
                         "rgba(175, 47, 47, 0.2)"))))

(def filter-all
  (-> filter-anchor
      (update :id conj ::all)
      (assoc :is :all)
      (update :with conj a/void-todo)))

(def filter-active
  (-> filter-anchor
      (update :id conj ::active)
      (assoc :is :active)
      (update :with conj a/void-todo)))

(def filter-done
  (-> filter-anchor
      (update :id conj ::done)
      (assoc :is :done)
      (update :with conj a/void-todo)))
