document.addEventListener('DOMContentLoaded', function () {
  document.querySelectorAll('.tree-toggle').forEach(function (btn) {
    btn.addEventListener('click', function (e) {
      e.preventDefault();
      var target = document.querySelector(this.dataset.target);
      if (target) {
        target.classList.toggle('d-none');
        this.querySelector('.tree-icon').textContent =
          target.classList.contains('d-none') ? '\u25B6' : '\u25BC';
      }
    });
  });
});
