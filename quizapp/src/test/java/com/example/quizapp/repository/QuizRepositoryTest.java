package com.example.quizapp.repository;

import com.example.quizapp.entity.Quiz;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests with @DataJpaTest.
 * Contains 10+ CRUD tests for Quiz repository.
 */
@DataJpaTest
@ActiveProfiles("test")
class QuizRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuizRepository quizRepository;

    private Quiz testQuiz;

    @BeforeEach
    void setUp() {
        testQuiz = new Quiz();
        testQuiz.setTitle("Test Quiz");
        testQuiz.setDescription("Test Description");
        testQuiz.setTimeLimit(300);
        testQuiz.setShuffleQuestions(true);
        testQuiz.setShuffleAnswers(false);
        testQuiz.setNegativePoints(false);
    }

    // ============ CREATE Tests ============

    @Test
    @DisplayName("Should save quiz successfully")
    void save_ShouldPersistQuiz() {
        // When
        Quiz saved = quizRepository.save(testQuiz);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test Quiz");
    }

    @Test
    @DisplayName("Should save quiz with all fields")
    void save_ShouldPersistAllFields() {
        // When
        Quiz saved = quizRepository.save(testQuiz);
        entityManager.flush();
        entityManager.clear();

        // Then
        Quiz found = entityManager.find(Quiz.class, saved.getId());
        assertThat(found.getTitle()).isEqualTo("Test Quiz");
        assertThat(found.getDescription()).isEqualTo("Test Description");
        assertThat(found.getTimeLimit()).isEqualTo(300);
        assertThat(found.getShuffleQuestions()).isTrue();
    }

    // ============ READ Tests ============

    @Test
    @DisplayName("Should find quiz by ID")
    void findById_ShouldReturnQuiz() {
        // Given
        Quiz saved = entityManager.persistAndFlush(testQuiz);

        // When
        Optional<Quiz> found = quizRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Quiz");
    }

    @Test
    @DisplayName("Should return empty when quiz not found")
    void findById_ShouldReturnEmpty_WhenNotFound() {
        // When
        Optional<Quiz> found = quizRepository.findById(999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find all quizzes")
    void findAll_ShouldReturnAllQuizzes() {
        // Given
        entityManager.persist(testQuiz);
        Quiz quiz2 = new Quiz();
        quiz2.setTitle("Quiz 2");
        entityManager.persistAndFlush(quiz2);

        // When
        List<Quiz> quizzes = quizRepository.findAll();

        // Then
        assertThat(quizzes).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should find quizzes with pagination")
    void findAll_WithPagination_ShouldReturnPage() {
        // Given
        for (int i = 0; i < 5; i++) {
            Quiz q = new Quiz();
            q.setTitle("Quiz " + i);
            entityManager.persist(q);
        }
        entityManager.flush();

        // When
        Page<Quiz> page = quizRepository.findAll(PageRequest.of(0, 3));

        // Then
        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(5);
    }

    // ============ Custom Query Tests ============

    @Test
    @DisplayName("Should find quiz by title containing (case insensitive)")
    void findByTitleContainingIgnoreCase_ShouldReturnMatches() {
        // Given
        entityManager.persist(testQuiz);
        Quiz quiz2 = new Quiz();
        quiz2.setTitle("Java Basics");
        entityManager.persistAndFlush(quiz2);

        // When
        List<Quiz> found = quizRepository.findByTitleContainingIgnoreCase("test");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getTitle()).isEqualTo("Test Quiz");
    }

    @Test
    @DisplayName("Should check if quiz exists by ID")
    void existsById_ShouldReturnTrue_WhenExists() {
        // Given
        Quiz saved = entityManager.persistAndFlush(testQuiz);

        // When
        boolean exists = quizRepository.existsById(saved.getId());

        // Then
        assertThat(exists).isTrue();
    }

    // ============ UPDATE Tests ============

    @Test
    @DisplayName("Should update quiz title")
    void update_ShouldModifyQuiz() {
        // Given
        Quiz saved = entityManager.persistAndFlush(testQuiz);

        // When
        saved.setTitle("Updated Title");
        quizRepository.save(saved);
        entityManager.flush();
        entityManager.clear();

        // Then
        Quiz found = entityManager.find(Quiz.class, saved.getId());
        assertThat(found.getTitle()).isEqualTo("Updated Title");
    }

    // ============ DELETE Tests ============

    @Test
    @DisplayName("Should delete quiz by ID")
    void deleteById_ShouldRemoveQuiz() {
        // Given
        Quiz saved = entityManager.persistAndFlush(testQuiz);
        Long id = saved.getId();

        // When
        quizRepository.deleteById(id);
        entityManager.flush();

        // Then
        Quiz found = entityManager.find(Quiz.class, id);
        assertThat(found).isNull();
    }

    @Test
    @DisplayName("Should count quizzes")
    void count_ShouldReturnCorrectCount() {
        // Given
        long initialCount = quizRepository.count();
        entityManager.persist(testQuiz);
        
        Quiz quiz2 = new Quiz();
        quiz2.setTitle("Another Quiz");  // Title is required
        entityManager.persistAndFlush(quiz2);

        // When
        long count = quizRepository.count();

        // Then
        assertThat(count).isEqualTo(initialCount + 2);
    }
}
