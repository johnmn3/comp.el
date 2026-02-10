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

      // Click the toggle-all arrow icon
      const toggleAll = page.locator('[data-testid="KeyboardArrowDownIcon"]').first();
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
      await editInput.press('Escape');

      // Should revert to original
      await expect(page.locator('text=Keep me')).toBeVisible();
    });

    test('should save on blur', async ({ page }) => {
      await addTodo(page, 'Blur save');

      await page.locator('text=Blur save').dblclick();

      const editInput = page.locator('input[value="Blur save"]');
      await editInput.fill('Blurred');
      await editInput.blur();

      await expect(page.locator('text=Blurred')).toBeVisible();
    });

    test('should delete if edited to empty', async ({ page }) => {
      await addTodo(page, 'Remove via edit');

      await page.locator('text=Remove via edit').dblclick();

      const editInput = page.locator('input[value="Remove via edit"]');
      await editInput.fill('');
      await editInput.press('Enter');

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

      await page.reload();
      await page.waitForSelector('text=Persistent todo', { timeout: 10000 });

      await expect(page.locator('text=Persistent todo')).toBeVisible();
    });
  });
});
