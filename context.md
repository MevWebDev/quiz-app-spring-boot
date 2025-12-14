# 📋 Pełna Specyfikacja Projektu Spring Boot

## System Quizowy („project-5”)

---

## I. Wymagania Ogólne (Common)

### A. Model Danych, Repository i JdbcTemplate

1. **Encje JPA**

   - Wymagane adnotacje:
     - `@Entity`
     - `@Id`
     - `@GeneratedValue`
     - `@Column`
   - Relacje:
     - `@OneToMany` / `@ManyToOne` z `@JoinColumn`
     - `@ManyToMany` z `@JoinTable`

2. **Repository JPA**

   - Interfejsy muszą rozszerzać `JpaRepository<T, ID>`
   - Wymagane custom query methods:
     - `findBy...`
     - lub `@Query`
   - Obsługa paginacji:
     - zwracanie obiektu `Page<T>`

3. **Konfiguracja Danych**

   - Konfiguracja w pliku `application.yml`:
     - datasource
     - jpa
     - hibernate
   - Inicjalizacja bazy danych:
     - pliki `.sql`

4. **JdbcTemplate**
   - Dodanie dependency `JdbcTemplate`
   - Zapytania odczytu:
     - `query()`
     - `RowMapper`
   - Operacje modyfikujące:
     - `INSERT`
     - `UPDATE`
     - `DELETE`
     - metoda `update()`
   - Implementacja:
     - warstwa serwisu **lub**
     - dedykowane DAO

---

### B. REST API

1. **Endpointy CRUD**

   - Kontrolery z adnotacją `@RestController`
   - Mapowanie:
     - `@RequestMapping("/api/v1/...")`
   - Metody:
     - `GET` – lista (z paginacją) i pojedynczy zasób
     - `POST` – tworzenie
     - `PUT` – aktualizacja
     - `DELETE` – usuwanie

2. **Obsługa HTTP**

   - Zwracanie danych przez `ResponseEntity`
   - Poprawne kody HTTP:
     - `200 OK`
     - `201 Created`
     - `204 No Content`
     - `400 Bad Request`
     - `404 Not Found`
   - Wymagane adnotacje:
     - `@PathVariable`
     - `@RequestBody`
     - `@RequestParam`

3. **Dokumentacja OpenAPI**
   - Włączenie Springdoc OpenAPI
   - Swagger UI dostępny pod:
     - `/swagger-ui.html`
   - Pełna dokumentacja całego API

---

### C. Warstwa Aplikacji (Business Logic i Widoki)

1. **Warstwa Serwisu**

   - Klasy z adnotacją `@Service`
   - Dependency Injection przez konstruktor
   - Transakcje:
     - `@Transactional(readOnly = true)`
     - `@Transactional(readOnly = false)`

2. **Mapowanie i DTO**

   - Mapowanie:
     - Entity ⇄ DTO
   - Brak bezpośredniego zwracania encji w API

3. **Obsługa Błędów**

   - Własne wyjątki:
     - np. `ResourceNotFoundException`
   - Globalna obsługa:
     - `@RestControllerAdvice`
     - `@ExceptionHandler`

4. **Walidacja DTO**

   - Bean Validation:
     - `@NotNull`
     - `@NotBlank`
     - `@Size`
     - `@Email`
     - `@Valid`
   - Walidacja:
     - na poziomie kontrolera
     - na poziomie serwisu
   - Spójne komunikaty błędów

5. **Widoki Thymeleaf (MVC)**

   - Kontrolery z `@Controller`
   - Przekazywanie danych:
     - `Model`
     - `@ModelAttribute`
   - Widoki:
     - `th:each` – listy
     - `th:object` / `th:field` – formularze
     - `th:errors` – walidacja

6. **Styling i Layout**

   - Layout oparty na fragmentach:
     - `th:fragment`
     - `th:replace`
   - Styling:
     - **Bootstrap 5**

7. **Obsługa Plików**
   - Upload:
     - `MultipartFile`
     - `enctype="multipart/form-data"`
   - Zapis na dysk:
     - `Files.copy`
   - Pobieranie / eksport:
     - `Resource`
     - `ResponseEntity<byte[]>`
     - CSV / PDF

---

### D. Spring Security

1. **Konfiguracja Security**

   - `SecurityFilterChain` jako `@Bean`
   - Kontrola dostępu:
     - `authorizeHttpRequests`
     - `requestMatchers`
   - Logowanie:
     - `formLogin`

2. **Uwierzytelnianie**
   - Hasła:
     - `BCryptPasswordEncoder`
   - Użytkownicy:
     - implementacja `UserDetailsService`
     - metoda `loadUserByUsername`

---

### E. Testowanie

1. **Testy Repozytorium**

   - Adnotacja:
     - `@DataJpaTest`
   - Minimum:
     - **10 testów CRUD**
   - Zakres:
     - custom queries
     - `RowMapper` (JDBC)

2. **Testy Jednostkowe (Serwis)**

   - Mockito:
     - `@Mock`
     - `@InjectMocks`
     - `when().thenReturn()`
     - `verify()`
   - Testy logiki biznesowej

3. **Testy Integracyjne (Kontrolery)**

   - Adnotacje:
     - `@WebMvcTest` **lub**
     - `@SpringBootTest`
   - Narzędzia:
     - `MockMvc`
     - `perform()`
     - `andExpect()`
   - Security:
     - `@WithMockUser`
   - Minimum:
     - **5 scenariuszy biznesowych**

4. **Pokrycie Kodu**
   - Narzędzie:
     - **JaCoCo**
   - Wymagane pokrycie:
     - **70%+**
   - Testy:
     - Happy Path
     - Error Cases

---

## II. Wymagania Specyficzne dla Systemu Quizowego

1. **Przepływ Gry**

   - Strona główna:
     - lista dostępnych quizów
   - Użytkownik:
     - wybiera quiz
     - podaje Nick (bez rejestracji)
   - Pytania:
     - sekwencyjnie **lub**
     - wszystkie naraz
   - Po zakończeniu:
     - wynik
     - zapis do rankingu

2. **Typy Pytań (8)**

   - Jednokrotny wybór
   - Wielokrotny wybór
   - Prawda / Fałsz
   - Krótka odpowiedź
   - Lista wyboru
   - Luki
   - Sortowanie
   - Dopasowanie  
     _(wystarczy prosty mechanizm walidacji)_

3. **Zarządzanie Quizami**

   - Panel administracyjny
   - Formularze:
     - dodawanie quizów
     - dodawanie pytań
     - dodawanie odpowiedzi

4. **Opcje Quizu**

   - Losowa kolejność pytań
   - Losowa kolejność odpowiedzi
   - Limit czasu:
     - sprawdzany przy wysyłce
   - Punkty ujemne
   - Blokada przycisku „Wstecz”:
     - logika frontendowa

5. **Ranking**

   - Tabela wyników:
     - Nick
     - Wynik
   - Dostęp:
     - po zakończeniu quizu
     - z menu głównego

6. **Punktacja i Walidacja**
   - Zabezpieczenie przed:
     - pustymi odpowiedziami
   - Poprawne liczenie punktów:
     - z uwzględnieniem punktów ujemnych
