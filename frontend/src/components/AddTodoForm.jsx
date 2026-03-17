/**
 * Form to create a new todo. Submits title to parent onSubmit handler.
 */
function AddTodoForm({ onSubmit }) {
  const handleSubmit = (e) => {
    e.preventDefault();
    const input = e.target.elements.title;
    const title = input?.value?.trim();
    if (!title) return;
    onSubmit(title);
    e.target.reset();
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginBottom: '1rem' }}>
      <input
        type="text"
        name="title"
        placeholder="Add new todo"
        aria-label="Todo title"
        style={{ marginRight: '0.5rem', padding: '0.5rem' }}
      />
      <button type="submit">Add</button>
    </form>
  );
}

export default AddTodoForm;
