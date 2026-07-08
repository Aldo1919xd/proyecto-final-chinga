document.addEventListener('DOMContentLoaded', function () {

  // ========================================
  // Utilidades
  // ========================================

  function mostrarError(input, mensaje) {
    var container = input.closest('.mb-3, .col');
    if (!container) return;
    var feedback = container.querySelector('.invalid-feedback');
    input.classList.add('is-invalid');
    if (feedback) {
      feedback.textContent = mensaje;
    }
  }

  function limpiarError(input) {
    input.classList.remove('is-invalid');
    var container = input.closest('.mb-3, .col');
    if (!container) return;
    var feedback = container.querySelector('.invalid-feedback');
    if (feedback) {
      feedback.textContent = '';
    }
  }

  function validarRequerido(input) {
    if (input.hasAttribute('required') && (!input.value || input.value.trim() === '')) {
      mostrarError(input, 'Este campo es obligatorio');
      return false;
    }
    limpiarError(input);
    return true;
  }

  // ========================================
  // Validacion en tiempo real (blur + input)
  // ========================================

  document.querySelectorAll('.form-control, .form-select').forEach(function (el) {
    el.addEventListener('blur', function () {
      validarRequerido(this);
      var validator = this.getAttribute('data-val');
      if (validator && window['val_' + validator]) {
        window['val_' + validator](this);
      }
    });
    el.addEventListener('input', function () {
      if (this.classList.contains('is-invalid')) {
        validarRequerido(this);
        var validator = this.getAttribute('data-val');
        if (validator && window['val_' + validator]) {
          window['val_' + validator](this);
        }
      }
    });
  });

  // ========================================
  // Validadores especificos (data-val)
  // ========================================

  // DNI: exactamente 8 digitos
  window.val_dni = function (input) {
    if (!input.value) return;
    if (!/^\d{8}$/.test(input.value)) {
      mostrarError(input, 'El DNI debe tener exactamente 8 digitos');
    } else {
      limpiarError(input);
    }
  };

  // RUC: exactamente 11 digitos
  window.val_ruc = function (input) {
    if (!input.value) return;
    if (!/^\d{11}$/.test(input.value)) {
      mostrarError(input, 'El RUC debe tener exactamente 11 digitos');
    } else {
      limpiarError(input);
    }
  };

  // CE: maximo 12 alfanumerico
  window.val_ce = function (input) {
    if (!input.value) return;
    if (!/^[a-zA-Z0-9]{1,12}$/.test(input.value)) {
      mostrarError(input, 'El CE debe tener maximo 12 caracteres alfanumericos');
    } else {
      limpiarError(input);
    }
  };

  // Numero documento generico por tipo
  window.val_numeroDocumento = function (input) {
    var row = input.closest('tr, .mb-3, .col');
    var tipoSelect = row ? row.querySelector('[name$="tipoDocumento.codTipoDocumento"], .tipo-doc-select') : null;
    if (!tipoSelect) tipoSelect = document.querySelector('[name$="tipoDocumento.codTipoDocumento"]');
    if (!tipoSelect || !tipoSelect.value) return;

    var tipoText = tipoSelect.options[tipoSelect.selectedIndex] ? tipoSelect.options[tipoSelect.selectedIndex].text : '';
    var val = input.value;

    if (tipoText === 'DNI' && !/^\d{8}$/.test(val)) {
      mostrarError(input, 'El DNI debe tener exactamente 8 digitos');
    } else if (tipoText === 'RUC' && !/^\d{11}$/.test(val)) {
      mostrarError(input, 'El RUC debe tener exactamente 11 digitos');
    } else if (tipoText === 'CE' && !/^[a-zA-Z0-9]{1,12}$/.test(val)) {
      mostrarError(input, 'El CE debe tener maximo 12 caracteres alfanumericos');
    } else if (tipoText === 'Pasaporte' && (val.length < 5 || val.length > 15)) {
      mostrarError(input, 'El Pasaporte debe tener entre 5 y 15 caracteres');
    } else {
      limpiarError(input);
    }
  };

  // Precio > 0
  window.val_precio = function (input) {
    if (!input.value) return;
    var num = parseFloat(input.value);
    if (isNaN(num) || num <= 0) {
      mostrarError(input, 'El precio debe ser mayor a 0');
    } else {
      limpiarError(input);
    }
  };

  // Entero positivo
  window.val_positivo = function (input) {
    if (!input.value) return;
    var num = parseInt(input.value, 10);
    if (isNaN(num) || num < 0) {
      mostrarError(input, 'El valor no puede ser negativo');
    } else {
      limpiarError(input);
    }
  };

  // Fecha no futura
  window.val_fechaPasada = function (input) {
    if (!input.value) return;
    var fecha = new Date(input.value);
    if (fecha > new Date()) {
      mostrarError(input, 'La fecha no puede ser futura');
    } else {
      limpiarError(input);
    }
  };

  // Username: minimo 3, alfanumerico
  window.val_username = function (input) {
    if (!input.value) return;
    if (input.value.length < 3) {
      mostrarError(input, 'El usuario debe tener al menos 3 caracteres');
    } else if (!/^[a-zA-Z0-9]+$/.test(input.value)) {
      mostrarError(input, 'Solo se permiten letras y numeros');
    } else {
      limpiarError(input);
    }
  };

  // Password minimo 6
  window.val_password = function (input) {
    if (!input.value) return;
    if (input.value.length < 6) {
      mostrarError(input, 'La contrasena debe tener al menos 6 caracteres');
    } else {
      limpiarError(input);
    }
  };

  // ========================================
  // Validacion de formulario al submit
  // ========================================

  document.querySelectorAll('form').forEach(function (form) {
    form.addEventListener('submit', function (e) {
      var firstError = null;

      this.querySelectorAll('.is-invalid').forEach(function (el) {
        el.classList.remove('is-invalid');
      });

      this.querySelectorAll('[required]').forEach(function (el) {
        if (!el.value || el.value.trim() === '') {
          mostrarError(el, 'Este campo es obligatorio');
          if (!firstError) firstError = el;
        }
      });

      this.querySelectorAll('[data-val]').forEach(function (el) {
        var validator = el.getAttribute('data-val');
        if (window['val_' + validator]) {
          window['val_' + validator](el);
          if (el.classList.contains('is-invalid') && !firstError) {
            firstError = el;
          }
        }
      });

      // Validacion especifica: al menos un nombre o razon social
      var nombreCliente = this.querySelector('[name="nombreCliente"]');
      var razonSocial = this.querySelector('[name="razonSocial"]');
      if (nombreCliente && razonSocial) {
        var nc = nombreCliente.value.trim();
        var rs = razonSocial.value.trim();
        if (!nc && !rs) {
          mostrarError(nombreCliente, 'Debe ingresar al menos un nombre o razon social');
          if (!firstError) firstError = nombreCliente;
        }
      }

      // Validacion especifica: al menos una cantidad mayor a 0 en ingreso
      var cantUnd = this.querySelector('[name="cantidadUnidad"]');
      var cantFracc = this.querySelector('[name="cantidadFraccion"]');
      if (cantUnd && cantFracc) {
        var cu = parseInt(cantUnd.value, 10) || 0;
        var cf = parseInt(cantFracc.value, 10) || 0;
        if (cu === 0 && cf === 0) {
          mostrarError(cantUnd, 'Debe ingresar al menos una unidad o fraccion');
          if (!firstError) firstError = cantUnd;
        }
      }

      if (firstError) {
        e.preventDefault();
        firstError.focus();
      }
    });
  });

  // ========================================
  // Reactividad: cambiar validador segun tipo doc
  // ========================================

  var tipoDocSelect = document.querySelector('[name="tipoDocumento.codTipoDocumento"]');
  var numDocInput = document.querySelector('[name="numeroDocumento"]');
  if (tipoDocSelect && numDocInput) {
    function actualizarValidadorDoc() {
      var tipoText = tipoDocSelect.options[tipoDocSelect.selectedIndex]
          ? tipoDocSelect.options[tipoDocSelect.selectedIndex].text : '';
      numDocInput.setAttribute('data-val', 'numeroDocumento');
      limpiarError(numDocInput);
    }
    tipoDocSelect.addEventListener('change', actualizarValidadorDoc);
    actualizarValidadorDoc();
  }

  // ========================================
  // Filtros de entrada
  // ========================================

  document.querySelectorAll('.solo-numeros').forEach(function (el) {
    el.addEventListener('input', function () {
      this.value = this.value.replace(/\D/g, '');
    });
  });

  document.querySelectorAll('.solo-letras').forEach(function (el) {
    el.addEventListener('input', function () {
      this.value = this.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s]/g, '');
    });
  });

  document.querySelectorAll('.sin-especiales').forEach(function (el) {
    el.addEventListener('input', function () {
      this.value = this.value.replace(/[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\s\-_.,]/g, '');
    });
  });

  document.querySelectorAll('input[type="number"]').forEach(function (el) {
    el.addEventListener('blur', function () {
      if (this.value && parseFloat(this.value) < 0) {
        this.value = Math.abs(parseFloat(this.value));
      }
    });
  });

});
