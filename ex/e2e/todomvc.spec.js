// @ts-check
const { test, expect } = require('@playwright/test');

// Helper: clear localStorage and reload to start fresh
async function freshStart(page) {
  await page.goto('/');
  await page.evaluate(() => localStorage.clear());
  await page.reload();
  await page.waitForSelector('input[placeholder="What needs to be done?"]', { timeout: 10000 });
}

// Helper: add a todo item
async function addTodo(page, text) {
  const input = page.locator('input[placeholder="What needs to be done?"]');
  await input.fill(text);
  await input.press('Enter');
}

test.describe('TodoMVC - comp.el', () => {

  test.beforeEach(async ({ page }) => {
    await freshStart(page);
  });

  test.describe('New Todo', () => {

    test('should show the input field', async ({ page }) => {
      const input = page.locator('input[placeholder="What needs to be done?"]');
      await expect(input).toBeVisible();
    });

    test('should add a new todo', async ({ page }) => {
      await addTodo(page, 'Buy groceries');

      // The todo text should appear in the list
      await expect(page.locator('text=Buy groceries')).toBeVisible();
    });

    test('should add multiple todos', async ({ page }) => {
      await addTodo(page, 'First todo');
      await addTodo(page, 'Second todo');
      await addTodo(page, 'Third todo');

      await expect(page.locator('text=First todo')).toBeVisible();
      await expect(page.locator('text=Second todo')).toBeVisible();
      await expect(page.locator('text=Third todo')).toBeVisible();
    });

    test('should clear input after adding a todo', async ({ page }) => {
      const input = page.locator('input[placeholder="What needs to be done?"]');
      await addTodo(page, 'A todo');

      // Input should be cleared after adding
      await expect(input).toHaveValue('');
    });

    test('should not add empty todos', async ({ page }) => {
      const input = page.locator('input[placeholder="What needs to be done?"]');
      await input.press('Enter');

      // No list items should appear
      const items = page.locator('[class*="MuiListItem"]');
      await expect(items).toHaveCount(0);
    });

    test('should trim whitespace from todo text', async ({ page }) => {
      await addTodo(page, '   Trimmed todo   ');

      await expect(page.locator('text=Trimmed todo')).toBeVisible();
    });
  });

  test.describe('Toggle Todo', () => {

    test('should toggle a todo as completed', async ({ page }) => {
      await addTodo(page, 'Toggle me');

      // Find and click the toggle (the circle/checkbox span)
      const todoItem = page.locator('text=Toggle me').locator('..');
      const toggle = todoItem.locator('span').first();
      await toggle.click();

      // After toggling, the text should have line-through style
      const label = page.locator('text=Toggle me');
      await expect(label).toHaveCSS('text-decoration-line', 'line-through');
    });

    test('should un-toggle a completed todo', async ({ page }) => {
      await addTodo(page, 'Toggle twice');

      const todoItem = page.locator('text=Toggle twice').locator('..');
      const toggle = todoItem.locator('span').first();

      // Toggle on
      await toggle.click();
      await expect(page.locator('text=Toggle twice')).toHaveCSS('text-decoration-line', 'line-through');

      // Toggle off
      await toggle.click();
      await expect(page.locator('text=Toggle twice')).not.toHaveCSS('text-decoration-line', 'line-through');
    });
  });

  test.describe('Toggle All', () => {

    test('should mark all todos as completed', async ({ page }) => {
      await addTodo(page, 'Todo A');
      await addTodo(page, 'Todo B');
      await addTodo(page, 'Todo C');

      // Click the toggle-all arrow icon (MUI grid item containing the SVG)
      const toggleAll = page.locator('.MuiGrid-grid-xs-1').first();
      await toggleAll.click();

      // All todos should be completed (line-through)
      const labels = page.locator('label');
      for (const label of await labels.all()) {
        await expect(label).toHaveCSS('text-decoration-line', 'line-through');
      }
    });
  });

  test.describe('Delete Todo', () => {

    test('should delete a todo on hover and click delete', async ({ page }) => {
      await addTodo(page, 'Delete me');

      // Hover over the todo item to reveal the delete button
      const todoText = page.locator('text=Delete me');
      await todoText.hover();

      // Click the × delete button
      const deleteBtn = page.locator('text=×');
      await deleteBtn.click();

      // The todo should be gone
      await expect(page.locator('text=Delete me')).toHaveCount(0);
    });
  });

  test.describe('Edit Todo', () => {

    test('should enter edit mode on double-click', async ({ page }) => {
      await addTodo(page, 'Edit me');

      // Double-click the todo label to enter edit mode
      await page.locator('text=Edit me').dblclick();

      // An input with the todo text should appear
      const editInput = page.locator('input[value="Edit me"]');
      await expect(editInput).toBeVisible();
    });

    test('should save on Enter', async ({ page }) => {
      await addTodo(page, 'Original text');

      await page.locator('text=Original text').dblclick();

      const editInput = page.locator('input[value="Original text"]');
      await editInput.fill('Updated text');
      await editInput.press('Enter');

      await expect(page.locator('text=Updated text')).toBeVisible();
      await expect(page.locator('text=Original text')).toHaveCount(0);
    });

    test('should cancel on Escape', async ({ page }) => {
      await addTodo(page, 'Keep me');

      await page.locator('text=Keep me').dblclick();

      const editInput = page.locator('input[value="Keep me"]');
      await editInput.fill('Changed');
      // After fill changes value, use updated locator for Escape
      await page.locator('input[value="Changed"]').press('Escape');

      // Should revert to original
      await expect(page.locator('text=Keep me')).toBeVisible();
    });

    test('should save on blur', async ({ page }) => {
      await addTodo(page, 'Blur save');

      await page.locator('text=Blur save').dblclick();

      const editInput = page.locator('input[value="Blur save"]');
      await editInput.fill('Blurred');
      // After fill changes value, use updated locator for blur
      await page.locator('input[value="Blurred"]').blur();

      await expect(page.locator('text=Blurred')).toBeVisible();
    });

    test('should delete if edited to empty', async ({ page }) => {
      await addTodo(page, 'Remove via edit');

      await page.locator('text=Remove via edit').dblclick();

      const editInput = page.locator('input[value="Remove via edit"]');
      await editInput.fill('');
      // After clearing, press Enter via keyboard (locator value changed)
      await page.keyboard.press('Enter');

      await expect(page.locator('text=Remove via edit')).toHaveCount(0);
    });
  });

  test.describe('Footer - Item Count', () => {

    test('should show correct item count', async ({ page }) => {
      await addTodo(page, 'Item 1');
      await expect(page.locator('text=1 item left')).toBeVisible();

      await addTodo(page, 'Item 2');
      await expect(page.locator('text=2 items left')).toBeVisible();
    });

    test('should not count completed items', async ({ page }) => {
      await addTodo(page, 'Active');
      await addTodo(page, 'Done');

      // Toggle second one as done
      const items = page.locator('label');
      const secondToggle = page.locator('text=Done').locator('..').locator('span').first();
      await secondToggle.click();

      await expect(page.locator('text=1 item left')).toBeVisible();
    });
  });

  test.describe('Footer - Filters', () => {

    test('should filter active todos', async ({ page }) => {
      await addTodo(page, 'Active one');
      await addTodo(page, 'Done one');

      // Complete "Done one"
      const toggle = page.locator('text=Done one').locator('..').locator('span').first();
      await toggle.click();

      // Click Active filter
      await page.locator('a:has-text("Active")').click();

      await expect(page.locator('text=Active one')).toBeVisible();
      await expect(page.locator('label:has-text("Done one")')).toHaveCount(0);
    });

    test('should filter completed todos', async ({ page }) => {
      await addTodo(page, 'Active one');
      await addTodo(page, 'Done one');

      const toggle = page.locator('text=Done one').locator('..').locator('span').first();
      await toggle.click();

      // Click Completed filter
      await page.locator('a:has-text("Completed")').click();

      await expect(page.locator('label:has-text("Active one")')).toHaveCount(0);
      await expect(page.locator('text=Done one')).toBeVisible();
    });

    test('should show all todos with All filter', async ({ page }) => {
      await addTodo(page, 'Active one');
      await addTodo(page, 'Done one');

      const toggle = page.locator('text=Done one').locator('..').locator('span').first();
      await toggle.click();

      // Go to Active, then back to All
      await page.locator('a:has-text("Active")').click();
      await page.locator('a:has-text("All")').click();

      await expect(page.locator('text=Active one')).toBeVisible();
      await expect(page.locator('text=Done one')).toBeVisible();
    });
  });

  test.describe('Footer - Clear Completed', () => {

    test('should clear completed todos', async ({ page }) => {
      await addTodo(page, 'Keep');
      await addTodo(page, 'Remove');

      // Complete "Remove"
      const toggle = page.locator('text=Remove').locator('..').locator('span').first();
      await toggle.click();

      // Click "Clear completed"
      await page.locator('text=Clear completed').click();

      await expect(page.locator('text=Keep')).toBeVisible();
      await expect(page.locator('label:has-text("Remove")')).toHaveCount(0);
    });

    test('should not show Clear completed when nothing is done', async ({ page }) => {
      await addTodo(page, 'Active only');

      await expect(page.locator('text=Clear completed')).toHaveCount(0);
    });
  });

  test.describe('Persistence', () => {

    test('should persist todos across page reloads', async ({ page }) => {
      await addTodo(page, 'Persistent todo');

      // Wait for localStorage to be written before reloading
      await page.waitForTimeout(500);
      await page.reload();
      await page.waitForSelector('input[placeholder="What needs to be done?"]', { timeout: 10000 });
      // Give re-frame time to rehydrate from localStorage
      await page.waitForTimeout(1000);

      await expect(page.locator('text=Persistent todo')).toBeVisible();
    });
  });

  test.describe('Re-editing Todos', () => {

    test('should allow editing a todo multiple times', async ({ page }) => {
      await addTodo(page, 'First version');

      // First edit
      await page.locator('text=First version').dblclick();
      const editInput1 = page.locator('input[value="First version"]');
      await editInput1.fill('Second version');
      await page.locator('input[value="Second version"]').press('Enter');
      await expect(page.locator('text=Second version')).toBeVisible();

      // Second edit on same todo
      await page.locator('text=Second version').dblclick();
      const editInput2 = page.locator('input[value="Second version"]');
      await editInput2.fill('Third version');
      await page.locator('input[value="Third version"]').press('Enter');

      await expect(page.locator('text=Third version')).toBeVisible();
      await expect(page.locator('text=First version')).toHaveCount(0);
      await expect(page.locator('text=Second version')).toHaveCount(0);
    });

    test('should preserve edits after toggling completion', async ({ page }) => {
      await addTodo(page, 'Edit then toggle');

      // Edit the todo
      await page.locator('text=Edit then toggle').dblclick();
      const editInput = page.locator('input[value="Edit then toggle"]');
      await editInput.fill('Edited todo');
      await page.locator('input[value="Edited todo"]').press('Enter');
      await expect(page.locator('text=Edited todo')).toBeVisible();

      // Toggle it as complete
      const todoItem = page.locator('text=Edited todo').locator('..');
      const toggle = todoItem.locator('span').first();
      await toggle.click();

      // Text should still be the edited version, with line-through
      await expect(page.locator('text=Edited todo')).toBeVisible();
      await expect(page.locator('text=Edited todo')).toHaveCSS('text-decoration-line', 'line-through');
    });
  });

  test.describe('Toggle and Filter Count Interaction', () => {

    test('should update item count when toggling todos', async ({ page }) => {
      await addTodo(page, 'Todo 1');
      await addTodo(page, 'Todo 2');
      await addTodo(page, 'Todo 3');
      await expect(page.locator('text=3 items left')).toBeVisible();

      // Toggle first todo
      const toggle1 = page.locator('text=Todo 1').locator('..').locator('span').first();
      await toggle1.click();
      await expect(page.locator('text=2 items left')).toBeVisible();

      // Toggle second todo
      const toggle2 = page.locator('text=Todo 2').locator('..').locator('span').first();
      await toggle2.click();
      await expect(page.locator('text=1 item left')).toBeVisible();

      // Untoggle first todo
      await toggle1.click();
      await expect(page.locator('text=2 items left')).toBeVisible();
    });

    test('should show correct counts after filtering', async ({ page }) => {
      await addTodo(page, 'Active A');
      await addTodo(page, 'Done B');
      await addTodo(page, 'Active C');

      // Complete "Done B"
      const toggle = page.locator('text=Done B').locator('..').locator('span').first();
      await toggle.click();

      // Count should show 2 items left regardless of filter
      await expect(page.locator('text=2 items left')).toBeVisible();

      // Switch to Active filter - count should still show 2
      await page.locator('a:has-text("Active")').click();
      await expect(page.locator('text=2 items left')).toBeVisible();

      // Switch to Completed filter - count should still show 2
      await page.locator('a:has-text("Completed")').click();
      await expect(page.locator('text=2 items left')).toBeVisible();
    });

    test('should update visible todos correctly when toggling in filtered view', async ({ page }) => {
      await addTodo(page, 'Item X');
      await addTodo(page, 'Item Y');

      // Complete Item X
      const toggleX = page.locator('text=Item X').locator('..').locator('span').first();
      await toggleX.click();

      // Switch to Active filter
      await page.locator('a:has-text("Active")').click();
      await expect(page.locator('text=Item Y')).toBeVisible();
      await expect(page.locator('label:has-text("Item X")')).toHaveCount(0);

      // Switch to Completed filter
      await page.locator('a:has-text("Completed")').click();
      await expect(page.locator('text=Item X')).toBeVisible();
      await expect(page.locator('label:has-text("Item Y")')).toHaveCount(0);
    });
  });

  test.describe('Footer Visibility', () => {

    test('should show zero items when no todos exist', async ({ page }) => {
      // Footer is always visible; with no todos it shows "0 items left"
      await expect(page.locator('text=0 items left')).toBeVisible();
    });

    test('should show footer when todos exist and hide Clear completed when none done', async ({ page }) => {
      await addTodo(page, 'Only todo');
      await expect(page.locator('text=1 item left')).toBeVisible();
      await expect(page.locator('text=Clear completed')).toHaveCount(0);
    });

    test('should remove Clear completed after clearing all completed', async ({ page }) => {
      await addTodo(page, 'Stay');
      await addTodo(page, 'Go away');

      // Complete "Go away"
      const toggle = page.locator('text=Go away').locator('..').locator('span').first();
      await toggle.click();
      await expect(page.locator('text=Clear completed')).toBeVisible();

      // Clear completed
      await page.locator('text=Clear completed').click();

      // Clear completed should disappear since nothing is done now
      await expect(page.locator('text=Clear completed')).toHaveCount(0);
      await expect(page.locator('text=1 item left')).toBeVisible();
    });
  });

  test.describe('Rapid Operations', () => {

    test('should handle rapid sequential adds', async ({ page }) => {
      // Add 10 todos rapidly
      for (let i = 1; i <= 10; i++) {
        await addTodo(page, `Rapid ${i}`);
      }

      // All should be visible (use exact matching to avoid "Rapid 1" matching "Rapid 10")
      for (let i = 1; i <= 10; i++) {
        await expect(page.getByText(`Rapid ${i}`, { exact: true })).toBeVisible();
      }
      await expect(page.locator('text=10 items left')).toBeVisible();
    });

    test('should handle rapid toggles on the same todo', async ({ page }) => {
      await addTodo(page, 'Toggle rapidly');

      const todoItem = page.locator('text=Toggle rapidly').locator('..');
      const toggle = todoItem.locator('span').first();

      // Toggle rapidly 6 times (should end up completed since even number)
      for (let i = 0; i < 6; i++) {
        await toggle.click();
      }

      // After 6 toggles (even), should be back to completed state
      // (start: not done, 1: done, 2: not done, 3: done, 4: not done, 5: done, 6: not done)
      await expect(page.locator('text=Toggle rapidly')).not.toHaveCSS('text-decoration-line', 'line-through');
    });

    test('should handle adding and immediately deleting', async ({ page }) => {
      await addTodo(page, 'Ephemeral');
      await expect(page.locator('text=Ephemeral')).toBeVisible();

      // Hover and delete immediately
      await page.locator('text=Ephemeral').hover();
      await page.locator('text=×').click();

      await expect(page.locator('text=Ephemeral')).toHaveCount(0);
    });
  });

  test.describe('Edge Cases', () => {

    test('should handle special characters in todo text', async ({ page }) => {
      await addTodo(page, 'Todo with <html> & "quotes"');
      await expect(page.locator('text=Todo with <html> & "quotes"')).toBeVisible();
    });

    test('should handle very long todo text', async ({ page }) => {
      const longText = 'A'.repeat(200);
      await addTodo(page, longText);
      await expect(page.locator(`text=${longText}`)).toBeVisible();
    });

    test('should toggle all then untoggle all', async ({ page }) => {
      await addTodo(page, 'T1');
      await addTodo(page, 'T2');
      await addTodo(page, 'T3');

      const toggleAll = page.locator('.MuiGrid-grid-xs-1').first();

      // Toggle all on
      await toggleAll.click();
      const labels = page.locator('label');
      for (const label of await labels.all()) {
        await expect(label).toHaveCSS('text-decoration-line', 'line-through');
      }
      await expect(page.locator('text=0 items left')).toBeVisible();

      // Toggle all off
      await toggleAll.click();
      for (const label of await labels.all()) {
        await expect(label).not.toHaveCSS('text-decoration-line', 'line-through');
      }
      await expect(page.locator('text=3 items left')).toBeVisible();
    });

    test('should clear completed and keep active todos intact', async ({ page }) => {
      await addTodo(page, 'Active 1');
      await addTodo(page, 'Done 1');
      await addTodo(page, 'Active 2');
      await addTodo(page, 'Done 2');

      // Complete Done 1 and Done 2
      const toggleDone1 = page.locator('text=Done 1').locator('..').locator('span').first();
      await toggleDone1.click();
      const toggleDone2 = page.locator('text=Done 2').locator('..').locator('span').first();
      await toggleDone2.click();

      await expect(page.locator('text=2 items left')).toBeVisible();

      // Clear completed
      await page.locator('text=Clear completed').click();

      // Only active todos remain
      await expect(page.locator('text=Active 1')).toBeVisible();
      await expect(page.locator('text=Active 2')).toBeVisible();
      await expect(page.locator('label:has-text("Done 1")')).toHaveCount(0);
      await expect(page.locator('label:has-text("Done 2")')).toHaveCount(0);
      await expect(page.locator('text=2 items left')).toBeVisible();
    });

    test('should persist completed state across reload', async ({ page }) => {
      await addTodo(page, 'Persist complete');

      // Toggle as completed
      const toggle = page.locator('text=Persist complete').locator('..').locator('span').first();
      await toggle.click();
      await expect(page.locator('text=Persist complete')).toHaveCSS('text-decoration-line', 'line-through');

      // Reload
      await page.reload();
      await page.waitForSelector('text=Persist complete', { timeout: 10000 });

      // Should still be completed
      await expect(page.locator('text=Persist complete')).toHaveCSS('text-decoration-line', 'line-through');
    });
  });
});
