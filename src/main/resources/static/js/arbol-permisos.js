document.addEventListener('DOMContentLoaded', function () {
  var lista = document.querySelector('.tree-list');
  if (!lista) return;

  function ocultarDescendientes(idPadre) {
    document.querySelectorAll('li[data-parent="' + idPadre + '"]').forEach(function (hijo) {
      hijo.classList.add('d-none');
      var hijoId = hijo.getAttribute('data-id');
      if (hijoId) ocultarDescendientes(hijoId);
      var toggle = hijo.querySelector('.tree-toggle');
      if (toggle) {
        var icon = toggle.querySelector('.tree-icon');
        if (icon) icon.textContent = '\u25B6';
      }
    });
  }

  document.querySelectorAll('.tree-toggle').forEach(function (btn) {
    btn.addEventListener('click', function (e) {
      e.preventDefault();
      var id = this.getAttribute('data-tree-id');
      if (!id) return;
      var hijos = document.querySelectorAll('li[data-parent="' + id + '"]');
      var primerHijo = hijos[0];
      if (!primerHijo) return;
      var colapsando = !primerHijo.classList.contains('d-none');
      hijos.forEach(function (hijo) {
        hijo.classList.toggle('d-none');
      });
      if (colapsando) {
        hijos.forEach(function (hijo) {
          var hijoId = hijo.getAttribute('data-id');
          if (hijoId) ocultarDescendientes(hijoId);
        });
      }
      var icon = btn.querySelector('.tree-icon');
      if (icon) icon.textContent = colapsando ? '\u25B6' : '\u25BC';
    });
  });

  var raices = document.querySelectorAll('.tree-list > li');
  raices.forEach(function (raiz) {
    var id = raiz.getAttribute('data-id');
    if (id) ocultarDescendientes(id);
  });

  function findAllDescendants(id) {
    var result = [];
    var directos = document.querySelectorAll('li[data-parent="' + id + '"]');
    directos.forEach(function (hijo) {
      result.push(hijo);
      var hijoId = hijo.getAttribute('data-id');
      if (hijoId) result = result.concat(findAllDescendants(hijoId));
    });
    return result;
  }

  function findAncestors(li) {
    var result = [];
    var current = li;
    while (true) {
      var parentId = current.getAttribute('data-parent');
      if (!parentId) break;
      var parent = document.querySelector('li[data-id="' + parentId + '"]');
      if (!parent) break;
      result.push(parent);
      current = parent;
    }
    return result;
  }

  function getActionCheckboxes(li) {
    return li.querySelectorAll('input[type="checkbox"][name="ver"], input[type="checkbox"][name="crear"], input[type="checkbox"][name="editar"], input[type="checkbox"][name="eliminar"], input[type="checkbox"][name="imprimir"]');
  }

  function updateMasterCheckbox(li) {
    var master = li.querySelector('.tree-master-check');
    if (!master) return;
    var checks = getActionCheckboxes(li);
    var allChecked = checks.length > 0;
    checks.forEach(function (cb) {
      if (!cb.checked) allChecked = false;
    });
    master.checked = allChecked;
  }

  function propagatePermission(li, name, checked) {
    var id = li.getAttribute('data-id');

    updateMasterCheckbox(li);

    if (id) {
      var descendants = findAllDescendants(id);
      descendants.forEach(function (desc) {
        var childCb = desc.querySelector('input[type="checkbox"][name="' + name + '"]');
        if (childCb) childCb.checked = checked;
      });
    }

    var ancestors = findAncestors(li);
    ancestors.forEach(function (anc) {
      var ancCb = anc.querySelector('input[type="checkbox"][name="' + name + '"]');
      if (ancCb) {
        if (checked) {
          ancCb.checked = true;
        } else {
          var ancId = anc.getAttribute('data-id');
          var ancDescendants = findAllDescendants(ancId);
          var anyChecked = false;
          ancDescendants.forEach(function (desc) {
            var descCb = desc.querySelector('input[type="checkbox"][name="' + name + '"]');
            if (descCb && descCb.checked) anyChecked = true;
          });
          if (!anyChecked) ancCb.checked = false;
        }
      }
      updateMasterCheckbox(anc);
    });
  }

  lista.addEventListener('click', function (e) {
    var target = e.target.closest('.tree-checks-toggle');
    if (!target) return;
    var li = target.closest('li');
    if (!li) return;
    var checks = li.querySelector('.tree-checks');
    if (!checks) return;
    checks.classList.toggle('d-none');
  });

  lista.addEventListener('change', function (e) {
    if (e.target.type !== 'checkbox') return;
    var checkbox = e.target;
    var li = checkbox.closest('li');
    if (!li) return;

    if (checkbox.classList.contains('tree-master-check')) {
      var id = li.getAttribute('data-id');
      var checked = checkbox.checked;
      var actionChecks = getActionCheckboxes(li);
      actionChecks.forEach(function (cb) {
        cb.checked = checked;
      });
      if (id) {
        var descendants = findAllDescendants(id);
        descendants.forEach(function (desc) {
          var descendantChecks = getActionCheckboxes(desc);
          descendantChecks.forEach(function (cb) {
            cb.checked = checked;
          });
        });
      }
      var actionNames = ['ver', 'crear', 'editar', 'eliminar', 'imprimir'];
      actionNames.forEach(function (name) {
        propagatePermission(li, name, checked);
      });
    } else {
      var name = checkbox.getAttribute('name');
      var checked = checkbox.checked;
      propagatePermission(li, name, checked);
    }
  });
});
