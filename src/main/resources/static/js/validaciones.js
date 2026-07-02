document.addEventListener('DOMContentLoaded', function () {

  // Only numbers
  document.querySelectorAll('.solo-numeros').forEach(function (el) {
    el.addEventListener('input', function () {
      this.value = this.value.replace(/\D/g, '');
    });
  });

  // Only letters and spaces
  document.querySelectorAll('.solo-letras').forEach(function (el) {
    el.addEventListener('input', function () {
      this.value = this.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s]/g, '');
    });
  });

  // Alphanumeric without special chars
  document.querySelectorAll('.sin-especiales').forEach(function (el) {
    el.addEventListener('input', function () {
      this.value = this.value.replace(/[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\s\-_.,]/g, '');
    });
  });

  // Prevent negative numbers
  document.querySelectorAll('input[type="number"]').forEach(function (el) {
    el.addEventListener('blur', function () {
      if (this.value && parseFloat(this.value) < 0) {
        this.value = Math.abs(parseFloat(this.value));
      }
    });
  });

  // Form validation before submit
  document.querySelectorAll('form').forEach(function (form) {
    form.addEventListener('submit', function (e) {
      var firstError = null;
      this.querySelectorAll('.is-invalid').forEach(function (el) {
        el.classList.remove('is-invalid');
      });
      this.querySelectorAll('[required]').forEach(function (el) {
        if (!el.value || el.value.trim() === '') {
          el.classList.add('is-invalid');
          if (!firstError) firstError = el;
        }
      });
      if (firstError) {
        e.preventDefault();
        firstError.focus();
      }
    });
  });

  // Real-time validation feedback
  document.querySelectorAll('.form-control, .form-select').forEach(function (el) {
    el.addEventListener('blur', function () {
      if (this.hasAttribute('required') && !this.value) {
        this.classList.add('is-invalid');
      } else {
        this.classList.remove('is-invalid');
      }
    });
  });

});
