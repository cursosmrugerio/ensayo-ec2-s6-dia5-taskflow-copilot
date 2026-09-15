# Proyecto final · Semana 6 · GitHub Copilot

**Alumno:** Recorrido EC2 del Día 5 (ensayo, 14-sep-2026) · **Usuario de GitHub:** `cursosmrugerio`

## 1. Qué construí

| | Feature | Especificación |
|---|---|---|
| x | `PATCH /tasks/{id}/assignee` — cambiar el responsable | [`specs/assignee.md`](../specs/assignee.md) |

## 2. El pull request

- **URL del PR (mergeado):** https://github.com/cursosmrugerio/ensayo-ec2-s6-dia5-taskflow-copilot/pull/2
- **Commit del merge en `main`:** `af3f749 Merge pull request #2 from cursosmrugerio/feature/assignee-2`
- **Comentarios de Copilot code review:** 2 en el código (más 2 que Copilot dejó solo en el resumen de su revisión, «Suppressed comments»)

## 3. Cómo lo hice

| Paso | Qué hice | Evidencia |
|---|---|---|
| Rama y spec | `git switch -c feature/assignee-2` (desde el commit de la spec) y copié la spec a `specs/` | `git log --oneline 69d3675..feature/assignee-2` |
| Implementación | `copilot -p "/crear-endpoint-taskflow …"` con `gpt-5-mini` | `semana6/sesion-implementacion.md` (tiene la línea `Skill "crear-endpoint-taskflow" loaded successfully`) |
| Revisión | agente `revisor` sobre `semana6/proyecto-final.diff` | `semana6/revision.md` (`Veredicto: APROBADO`, 3 sugerencias) |
| Tests | `mvn test` en verde | `Tests run: 73, Failures: 0, Errors: 0, Skipped: 0` |
| Comprobación REST | `verificar.ps1` con `casos-assignee.ps1` | sección 5 de este documento |
| Code review | Copilot en el PR | la pestaña *Files changed* del PR |

## 4. Qué hizo el agente y qué corregí yo

| # | Qué hizo mal el agente (archivo) | Quién lo detectó | Cómo quedó corregido |
|---|---|---|---|
| 1 | Validaciones sin mensaje en español (`@NotNull` y `@Positive` sin `message`) — `TaskAssigneeUpdateRequest.java` | Copilot review | prompt del PF-6 con `semana6/code-review.md` (3.51 créditos), commit `ed746b4` |
| 2 | El stub de `reasignar` aceptaba cualquier `Long`: el test del 200 no comprobaba que el controller pasa el `assigneeId` — `ReasignarTareaControllerTest.java:48` | Copilot review | el prompt del PF-6 no lo aplicó («no se modificaron tests»); lo cambié a mano a `eq(2L)`, commit `5f15e24` |
| 3 | Javadoc falso: «Reutiliza la regla de dominio en Task», pero compara `getStatus() == DONE` en el service — `TaskService.java:99` | revisor (sugerencia 1) y Copilot (en su resumen) | sin corregir: es una sugerencia, queda anotada aquí |
| 4 | `com.taskflow.dto.TaskAssigneeUpdateRequest` con nombre completo en vez de `import` — `TaskController.java` | revisor (sugerencia 2) | sin corregir: sugerencia |
| 5 | `import` sin usar de `TaskAssigneeUpdateRequest` — `ReasignarTareaControllerTest.java` | revisor (sugerencia 3) | sin corregir: sugerencia |
| 6 | `verify(taskRepository, never()).save(t)` solo prohíbe guardar esa misma instancia — `ReasignarTareaServiceTest.java:57` | Copilot (en su resumen) | sin corregir |

**Lo que el agente hizo bien a la primera** (una o dos líneas): la lógica de `TaskService.reasignar` y el endpoint del controller (404, 422, 400 y 200) salieron conformes a la spec: los 8 casos REST de `casos-assignee.ps1` en `[OK]` sin tocarlos.

## 5. Comprobaciones REST

```text
Repositorio: C:\recorrido\dia5\taskflow-copilot-recorrido
URL de la app: http://127.0.0.1:8085
Empaquetando con Maven (mvn -q package -DskipTests), tarda unos segundos...
App arrancando (PID 1128). Esperando a que /info responda...
App lista en 23 s.
[FALLA] GET /tasks/overdue devuelve solo la tarea 7
        esperaba: HTTP 200 ids=7
        obtuve:   HTTP 400 ids=
[FALLA] GET /tasks/unassigned devuelve las tareas 4 y 6
        esperaba: HTTP 200 ids=4,6
        obtuve:   HTTP 400 ids=
[FALLA] GET /projects/1/summary
        esperaba: HTTP 200 projectId=1 totalTasks=5 TODO=3 IN_PROGRESS=1 DONE=1 overdue=0 projectNameOk=True
        obtuve:   HTTP 404 
[FALLA] GET /projects/2/summary
        esperaba: HTTP 200 projectId=2 totalTasks=4 TODO=1 IN_PROGRESS=2 DONE=1 overdue=1 projectNameOk=True
        obtuve:   HTTP 404 
[FALLA] GET /projects/3/summary
        esperaba: HTTP 200 projectId=3 totalTasks=0 TODO=0 IN_PROGRESS=0 DONE=0 overdue=0 projectNameOk=True
        obtuve:   HTTP 404 
[OK]    GET /projects/99/summary responde 404
[OK]    GET /projects/1/summary sin token responde 401
[OK]    PATCH /tasks/4/assignee asigna a luis
[OK]    GET /tasks/4 conserva el responsable nuevo
[OK]    PATCH /tasks/4/status a DONE ahora responde 200
[OK]    PATCH /tasks/2/assignee (DONE) responde 422
[OK]    PATCH /tasks/99/assignee responde 404
[OK]    PATCH /tasks/6/assignee con {} responde 400 y nombra assigneeId
[OK]    PATCH /tasks/6/assignee con 0 responde 400
[OK]    PATCH /tasks/6/assignee sin token responde 401
App detenida (PID 1128).
[OK]    App apagada: el puerto 8085 ya no responde
RESULTADO: 5 de 16 con FALLA. Logs de la app: C:\recorrido\dia5\taskflow-copilot-recorrido\target\verificar-8085-app.log
```

Las cinco `[FALLA]` son de los días 2 a 4: llegué por el «Si vienes atrasado» y no tengo esos endpoints.

## 6. Créditos de la semana

| Qué | AI credits |
|---|---|
| Usados en septiembre según github.com (incluye semanas anteriores si usaste Copilot antes) | no consultado en este ensayo (github.com/settings/billing no se abrió) |
| Implementación con la skill (`AI Credits` del PF-2) | 13.44 |
| Revisión del `revisor` (`AI Credits` del PF-3) | 2.1 |
| Correcciones del PF-4 y del PF-6, si hubo (`AI Credits`) | 3.51 (PF-4: 0, el veredicto fue APROBADO) |
