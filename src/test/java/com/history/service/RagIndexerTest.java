package com.history.service;

import com.history.entity.*;
import com.history.repository.*;
import com.history.service.impl.InMemoryVectorStore;
import com.history.config.RagProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * RagIndexer 单元测试 — 对应 Iteration #94 验收用例 T94-05
 *
 * 验证：ragIndexer.indexAll() 灌入后，向量数 = 事件+人物+朝代+知识+专题总数
 *
 * 策略：纯 Mockito，mock 所有 Repository 和 EmbeddingService，用真实 InMemoryVectorStore
 */
@ExtendWith(MockitoExtension.class)
class RagIndexerTest {

    @Mock
    private EmbeddingService embeddingService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private DynastyRepository dynastyRepository;

    @Mock
    private KnowledgeCardRepository knowledgeCardRepository;

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private RagIndexer ragIndexer;

    private InMemoryVectorStore vectorStore;

    private static final float[] FAKE_VECTOR = new float[]{0.1f, 0.2f, 0.3f};

    @BeforeEach
    void setUp() {
        // 真实 InMemoryVectorStore 实例
        vectorStore = new InMemoryVectorStore(new RagProperties());

        // 通过反射注入 vectorStore（@InjectMocks 无法注入非 @Mock 字段）
        try {
            var field = RagIndexer.class.getDeclaredField("vectorStore");
            field.setAccessible(true);
            field.set(ragIndexer, vectorStore);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * T94-05：indexAll() 灌入后，向量数 = 事件+人物+朝代+知识+专题总数
     */
    @Test
    void testIndexAll_T94_05() {
        // 准备 mock 数据
        DynastyEntity tang = DynastyEntity.builder()
                .id(1L).uid("tang").name("唐").period("618-907").build();
        DynastyEntity song = DynastyEntity.builder()
                .id(2L).uid("song").name("宋").period("960-1279").build();

        EventEntity event1 = EventEntity.builder()
                .id(1L).uid("an-lushan").title("安史之乱").year(755)
                .yearDisplay("公元755年").yearPrecision("exact")
                .category("战争").dynasty(tang)
                .description("安史之乱").build();

        PersonEntity person1 = PersonEntity.builder()
                .id(1L).uid("an-lushan-person").name("安禄山")
                .dynasty(tang).yearsDisplay("703-757").bio("唐代将领").build();

        KnowledgeCardEntity kc1 = KnowledgeCardEntity.builder()
                .id(1L).uid("kc1").title("知识卡片1").description("描述").build();

        TopicEntity topic1 = TopicEntity.builder()
                .id(1L).uid("topic1").title("专题1").summary("摘要").build();

        when(eventRepository.findAll()).thenReturn(List.of(event1));
        when(personRepository.findAll()).thenReturn(List.of(person1));
        when(dynastyRepository.findAll()).thenReturn(List.of(tang, song));
        when(knowledgeCardRepository.findAll()).thenReturn(List.of(kc1));
        when(topicRepository.findAll()).thenReturn(List.of(topic1));

        // EmbeddingService：批量接口根据输入长度返回等长向量列表
        when(embeddingService.embedBatch(anyList())).thenAnswer(inv -> {
            List<String> texts = inv.getArgument(0);
            List<float[]> result = new java.util.ArrayList<>();
            for (int i = 0; i < texts.size(); i++) {
                result.add(FAKE_VECTOR);
            }
            return result;
        });

        // 执行
        ragIndexer.indexAll();

        // 断言：1 event + 1 person + 2 dynasties + 1 knowledge + 1 topic = 6
        int expected = 1 + 1 + 2 + 1 + 1;
        assertThat(vectorStore.size())
                .as("向量数应等于数据库所有记录总数")
                .isEqualTo(expected);
    }

    /**
     * T94-05 补充：空数据库时 indexAll() 不报错，向量数为 0
     */
    @Test
    void testIndexAllWithEmptyDatabase_T94_05_empty() {
        when(eventRepository.findAll()).thenReturn(List.of());
        when(personRepository.findAll()).thenReturn(List.of());
        when(dynastyRepository.findAll()).thenReturn(List.of());
        when(knowledgeCardRepository.findAll()).thenReturn(List.of());
        when(topicRepository.findAll()).thenReturn(List.of());

        ragIndexer.indexAll();

        assertThat(vectorStore.size()).isZero();
    }

    /**
     * T94-05 补充：indexAll() 是幂等的，重复调用不会重复灌入
     */
    @Test
    void testIndexAllIsIdempotent_T94_05_idempotent() {
        DynastyEntity tang = DynastyEntity.builder()
                .id(1L).uid("tang").name("唐").build();
        when(dynastyRepository.findAll()).thenReturn(List.of(tang));
        when(eventRepository.findAll()).thenReturn(List.of());
        when(personRepository.findAll()).thenReturn(List.of());
        when(knowledgeCardRepository.findAll()).thenReturn(List.of());
        when(topicRepository.findAll()).thenReturn(List.of());
        when(embeddingService.embedBatch(anyList())).thenReturn(List.of(FAKE_VECTOR));

        ragIndexer.indexAll();
        int sizeAfterFirstCall = vectorStore.size();

        ragIndexer.indexAll();
        int sizeAfterSecondCall = vectorStore.size();

        assertThat(sizeAfterFirstCall).isEqualTo(1);
        assertThat(sizeAfterSecondCall)
                .as("重复灌入应保持相同大小（先 clear 再灌入）")
                .isEqualTo(sizeAfterFirstCall);
    }
}
