package com.history.initializer;

import com.history.entity.*;
import com.history.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 数据初始化器
 * 在应用启动时自动填充示例数据（仅在数据库为空时执行）
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final DynastyRepository dynastyRepo;
    private final EventRepository eventRepo;
    private final PersonRepository personRepo;
    private final KnowledgeCardRepository knowledgeCardRepo;

    @Override
    public void run(String... args) {
        if (dynastyRepo.count() > 0 && eventRepo.count() > 0 && personRepo.count() > 0) {
            log.info("数据已存在，跳过初始化");
            return;
        }
        log.info("开始初始化数据...");
        initDynasties();
        initEvents();
        initPersons();
        initKnowledgeCards();
        log.info("数据初始化完成");
    }

    private void initDynasties() {
        if (dynastyRepo.count() > 0) return;

        List<DynastyEntity> dynasties = Arrays.asList(
            DynastyEntity.builder()
                .uid("xia").name("夏").fullName("夏朝")
                .period("约前2070年—约前1600年")
                .periodStart(-2070).periodEnd(-1600)
                .founder("禹").lastRuler("桀")
                .capital("阳城（今河南登封）")
                .duration("约470年")
                .highlights("中国历史上第一个王朝，世袭制取代禅让制")
                .description("夏朝是中国史书中记载的第一个世袭制朝代。一般认为夏朝的家国历史大概为从公元前20世纪至公元前16世纪，共传十四世，十七王，延续约470年。")
                .fallReason("末代君主桀荒淫无道，失去民心")
                .legacy("开创了中国四干多年世袭制的先河")
                .build(),

            DynastyEntity.builder()
                .uid("shang").name("商").fullName("商朝")
                .period("约前1600年—前1046年")
                .periodStart(-1600).periodEnd(-1046)
                .founder("汤").lastRuler("纣王")
                .capital("殷（今河南安阳）")
                .duration("约554年")
                .highlights("甲骨文和金文的出现，青铜器工艺巅峰")
                .description("商朝是中国第二个朝代，约公元前1600年至公元前1046年。商汤灭夏后建立商朝，前期多次迁都，直到盘庚迁殷后才稳定下来。")
                .fallReason("纣王暴虐无道，众叛亲离")
                .legacy("甲骨文是中国最早的文字之一，青铜器工艺达到高峰")
                .build(),

            DynastyEntity.builder()
                .uid("zhou").name("周").fullName("西周")
                .period("前1046年—前256年")
                .periodStart(-1046).periodEnd(-256)
                .founder("周武王").lastRuler("周赧王")
                .capital("镐京（今陕西西安）")
                .duration("约790年")
                .highlights("礼乐制度、分封制、百家争鸣")
                .description("周朝是中国历史上最长的朝代，分为西周和东周两个时期。西周定都镐京，东周迁都洛邑。春秋战国时期思想文化空前繁荣。")
                .fallReason("周王室衰微，诸侯争霸，秦国统一六国")
                .legacy("奠定了中国传统文化的基础，儒家、道家、法家等学派在此时期形成")
                .build(),

            DynastyEntity.builder()
                .uid("qin").name("秦").fullName("秦朝")
                .period("前221年—前207年")
                .periodStart(-221).periodEnd(-207)
                .founder("秦始皇").lastRuler("子婴")
                .capital("咸阳（今陕西咸阳）")
                .duration("15年")
                .highlights("统一六国、书同文车同轨、修筑长城")
                .description("秦朝是中国历史上第一个统一的中央集权制封建王朝。秦始皇统一六国后，推行一系列改革措施，奠定了中国两千多年政治制度的基本格局。")
                .fallReason("暴政失民心，陈胜吴广起义后迅速崩溃")
                .legacy("建立了中央集权的官僚体系，修筑万里长城，统一文字度量衡")
                .build(),

            DynastyEntity.builder()
                .uid("han").name("西汉").fullName("西汉（前汉）")
                .period("前202年—公元8年")
                .periodStart(-202).periodEnd(8)
                .founder("汉高祖刘邦").lastRuler("孺子婴")
                .capital("长安（今陕西西安）")
                .duration("210年")
                .highlights("丝绸之路开通、独尊儒术、造纸术发明")
                .description("西汉是中国历史上强盛的王朝之一，开辟了丝绸之路，促进了中西方文化交流。汉武帝独尊儒术，确立了儒家思想的正统地位。")
                .fallReason("外戚王莽篡权，建立新朝")
                .legacy("奠定了汉族的主体地位，丝绸之路成为东西方交流的桥梁")
                .build(),

            DynastyEntity.builder()
                .uid("tang").name("唐").fullName("唐朝")
                .period("618年—907年")
                .periodStart(618).periodEnd(907)
                .founder("唐高祖李渊").lastRuler("哀帝李柷")
                .capital("长安（今陕西西安）")
                .duration("289年")
                .highlights("贞观之治、开元盛世、诗歌巅峰")
                .description("唐朝是中国古代最繁荣的朝代之一，被称为'大唐盛世'。唐代诗歌、书法、绘画、雕塑等艺术形式达到巅峰，长安是当时世界上最大的城市。")
                .fallReason("安史之乱后藩镇割据，最终被朱温所灭")
                .legacy("创造了灿烂辉煌的中华文化，影响了日本、朝鲜等周边国家")
                .build(),

            DynastyEntity.builder()
                .uid("song").name("宋").fullName("宋朝")
                .period("960年—1279年")
                .periodStart(960).periodEnd(1279)
                .founder("宋太祖赵匡胤").lastRuler("宋帝昺")
                .capital("开封/临安（今杭州）")
                .duration("319年")
                .highlights("四大发明中三项成熟于宋代，活字印刷术，指南针")
                .description("宋朝分为北宋和南宋两个时期，经济文化高度发达，科技成就突出。活字印刷术、指南针和火药在这一时期得到广泛应用。")
                .fallReason("军事积弱，先后被金、元所灭")
                .legacy("经济重心南移完成，市民文化繁荣，理学兴起")
                .build(),

            DynastyEntity.builder()
                .uid("yuan").name("元").fullName("元朝")
                .period("1271年—1368年")
                .periodStart(1271).periodEnd(1368)
                .founder("元世祖忽必烈").lastRuler("元顺帝")
                .capital("大都（今北京）")
                .duration("97年")
                .highlights("疆域最辽阔，行省制度，马可波罗游记")
                .description("元朝是由蒙古族建立的统一王朝，疆域空前辽阔，是中国历史上版图最大的朝代。行省制度对后世影响深远。")
                .fallReason("民族压迫政策引发民变，朱元璋起义推翻元朝")
                .legacy("奠定了中国现代版图的基础，促进了东西方文化交流")
                .build(),

            DynastyEntity.builder()
                .uid("ming").name("明").fullName("明朝")
                .period("1368年—1644年")
                .periodStart(1368).periodEnd(1644)
                .founder("明太祖朱元璋").lastRuler("崇祯帝")
                .capital("南京/北京")
                .duration("276年")
                .highlights("郑和下西洋、修筑长城、资本主义萌芽")
                .description("明朝是中国历史上最后一个由汉族建立的大一统王朝。郑和七下西洋展现了强大的航海实力，修筑了今天我们看到的长城主体部分。")
                .fallReason("农民起义和满清入侵双重压力，崇祯帝自缢")
                .legacy("奠定了现代中国的版图基础和文化格局")
                .build(),

            DynastyEntity.builder()
                .uid("qing").name("清").fullName("清朝")
                .period("1644年—1912年")
                .periodStart(1644).periodEnd(1912)
                .founder("清圣祖康熙").lastRuler("溥仪")
                .capital("北京")
                .duration("268年")
                .highlights("康乾盛世，疆域最大，文字狱")
                .description("清朝是中国历史上最后一个封建王朝，由满族建立。鼎盛时期疆域达1300多万平方公里，但后期闭关锁国导致落后于世界潮流。")
                .fallReason("内忧外患，辛亥革命推翻帝制")
                .legacy("奠定了现代中国版图的基础，但也留下了深刻的历史教训")
                .build(),

            DynastyEntity.builder()
                .uid("three-kingdoms").name("三国").fullName("三国时期")
                .period("220年—280年").periodStart(220).periodEnd(280)
                .founder("曹丕").lastRuler("刘禅/孙皓/曹奂")
                .capital("洛阳/成都/建业").duration("60年")
                .highlights("三国鼎立，人才辈出").description("三国时期是中国历史上重要的分裂时期，魏蜀吴三国鼎立，文化繁荣。")
                .fallReason("司马氏篡权建立晋朝").legacy("三国演义成为中国文学经典")
                .build(),

            DynastyEntity.builder()
                .uid("jin").name("晋").fullName("晋朝")
                .period("265年—420年").periodStart(265).periodEnd(420)
                .founder("司马炎").lastRuler("晋恭帝")
                .capital("洛阳/建康").duration("155年")
                .highlights("八王之乱、五胡乱华").description("晋朝分为西晋和东晋，西晋短暂统一后爆发八王之乱。")
                .fallReason("门阀政治腐败").legacy("促进了江南地区开发和玄学盛行")
                .build(),

            DynastyEntity.builder()
                .uid("sui").name("隋").fullName("隋朝")
                .period("581年—618年").periodStart(581).periodEnd(618)
                .founder("隋文帝杨坚").lastRuler("隋炀帝杨广")
                .capital("大兴（今西安）").duration("37年")
                .highlights("科举创立、大运河开凿").description("隋朝结束近三百年分裂，创立科举制度。")
                .fallReason("隋炀帝暴政").legacy("为唐朝繁荣奠定制度基础")
                .build()
        );

        dynastyRepo.saveAll(dynasties);
        log.info("初始化朝代数据：{} 条", dynasties.size());
    }

    private void initEvents() {
        if (eventRepo.count() > 0) return;

        List<EventEntity> events = Arrays.asList(
            // 夏商周
            EventEntity.builder()
                .uid("yu-jiangshan").title("大禹治水").year(-2100)
                .yearDisplay("约公元前2100年").yearPrecision("approx")
                .category("盛世")
                .tags(Arrays.asList("夏朝", "治水", "传说"))
                .description("大禹带领民众治理洪水，三过家门而不入，最终成功平定水患，赢得万民敬仰。")
                .fulltext("相传远古时期洪水泛滥，尧帝命鲧治水，九年无功。舜继位后命鲧之子禹继续治水。禹改变父亲堵截的方法，采用疏导为主的方式，历时十三年终于平息水患。")
                .build(),
            EventEntity.builder()
                .uid("xia-establish").title("夏朝建立").year(-2070)
                .yearDisplay("约公元前2070年").yearPrecision("approx")
                .category("朝代更迭")
                .tags(Arrays.asList("夏朝", "世袭制"))
                .description("禹死后，其子启继位，世袭制取代禅让制，标志着中国第一个王朝——夏朝的建立。")
                .fulltext("大禹治水成功后威望日隆，舜禅让于禹。禹去世后，本应传位给伯益，但其子启夺取了王位，从此'天下为公'变为'天下为家'，世袭制正式确立。")
                .build(),
            EventEntity.builder()
                .uid("tang-mie-xia").title("商汤灭夏").year(-1600)
                .yearDisplay("约公元前1600年").yearPrecision("approx")
                .category("朝代更迭")
                .tags(Arrays.asList("商朝", "灭夏"))
                .description("商汤率军伐夏，在鸣条之战中大败夏桀，建立商朝。")
                .fulltext("夏朝末年，桀荒淫无道，残害忠良，民怨沸腾。商部落首领汤趁机起兵，联合各方诸侯，在鸣条（今河南封丘附近）大败夏桀，夏朝灭亡，商朝建立。")
                .build(),
            EventEntity.builder()
                .uid("wu-gang-mie-shang").title("牧野之战·武王伐纣").year(-1046)
                .yearDisplay("约公元前1046年").yearPrecision("approx")
                .category("朝代更迭")
                .tags(Arrays.asList("周朝", "伐纣"))
                .description("周武王姬发率联军在牧野之战中击败商军，商纣王自焚而死，商朝灭亡，周朝建立。")
                .fulltext("商纣王暴虐无道，杀比干、囚箕子。周武王联合八百诸侯，在牧野（今河南淇县南）决战。商军倒戈，纣王鹿台自焚，商朝灭亡。")
                .build(),
            EventEntity.builder()
                .uid("zhuge-liang").title("周公制礼作乐").year(-1040)
                .yearDisplay("约公元前1040年").yearPrecision("approx")
                .category("文化")
                .tags(Arrays.asList("周朝", "礼乐"))
                .description("周公旦制定了一套完整的礼乐制度，奠定了中国古代社会秩序和文化传统的基础。")
                .fulltext("周武王死后，成王年幼，周公摄政。他制礼作乐，建立宗法制、分封制，奠定了中国古代政治制度的基础，被后世儒家尊为圣人。")
                .build(),

            // 秦汉
            EventEntity.builder()
                .uid("qin-unify").title("秦始皇统一六国").year(-221)
                .yearDisplay("公元前221年").yearPrecision("exact")
                .category("朝代更迭")
                .tags(Arrays.asList("秦朝", "统一"))
                .description("秦王嬴政先后灭韩、赵、魏、楚、燕、齐六国，完成统一大业，建立中国历史上第一个中央集权制王朝。")
                .fulltext("公元前230年至前221年，秦始皇先后灭掉六国，结束了春秋战国五百多年的分裂局面。他自称'始皇帝'，推行郡县制、书同文、车同轨、统一度量衡。")
                .build(),
            EventEntity.builder()
                .uid("qin-great-wall").title("修筑万里长城").year(-214)
                .yearDisplay("公元前214年").yearPrecision("exact")
                .category("盛世")
                .tags(Arrays.asList("秦朝", "防御"))
                .description("秦始皇下令连接和修缮各国长城，形成西起临洮、东至辽东的万里长城，抵御北方游牧民族侵扰。")
                .fulltext("秦始皇派蒙恬率三十万大军北击匈奴，同时征发百万民夫，将战国时期秦、赵、燕三国长城连接起来，修筑了西起临洮东至辽东的万里长城。")
                .build(),
            EventEntity.builder()
                .uid("chen-sheng-wu-guang").title("陈胜吴广起义").year(-209)
                .yearDisplay("公元前209年").yearPrecision("exact")
                .category("革命")
                .tags(Arrays.asList("秦朝", "起义"))
                .description("大泽乡起义爆发，陈胜吴广率九百戍卒揭竿而起，提出'王侯将相宁有种乎'，拉开了秦末农民战争的序幕。")
                .fulltext("公元前209年，陈胜、吴广等九百名戍卒被征发前往渔阳，在大泽乡遇雨误期。按照秦法误期当斩，二人遂揭竿而起，成为中国历史上第一次大规模农民起义。")
                .build(),
            EventEntity.builder()
                .uid("chu-han-zheng").title("楚汉之争").year(-206)
                .yearDisplay("公元前206年").yearPrecision("exact")
                .category("战争")
                .tags(Arrays.asList("汉朝", "争霸"))
                .description("刘邦与项羽争夺天下的战争，最终以垓下之战项羽自刎、刘邦获胜告终，建立了西汉王朝。")
                .fulltext("秦亡之后，项羽分封十八路诸侯，自封西楚霸王。刘邦暗中积蓄力量，明修栈道暗度陈仓，与项羽展开长达四年的楚汉之争，最终在垓下围困项羽。")
                .build(),
            EventEntity.builder()
                .uid("wu-di-dong-zhong-shu").title("汉武帝独尊儒术").year(-136)
                .yearDisplay("公元前136年").yearPrecision("exact")
                .category("文化")
                .tags(Arrays.asList("汉朝", "儒学"))
                .description("董仲舒提出'罢黜百家，独尊儒术'，汉武帝采纳建议，将儒家学说确立为国家正统思想，影响中国两千余年。")
                .fulltext("汉武帝时期，董仲舒提出'天人感应''君权神授'等理论，主张罢黜百家独尊儒术。汉武帝采纳其建议，设立太学，以儒家五经为教学内容，确立了儒学的正统地位。")
                .build(),
            EventEntity.builder()
                .uid("si-ma-qian").title("司马迁著《史记》").year(-91)
                .yearDisplay("约公元前91年").yearPrecision("approx")
                .category("文化")
                .tags(Arrays.asList("汉朝", "史学"))
                .description("司马迁忍辱负重，历时十余年完成中国第一部纪传体通史《史记》，被鲁迅誉为'史家之绝唱，无韵之离骚'。")
                .fulltext("司马迁继承父业任太史令，因李陵事件获罪受宫刑。他忍辱负重，发愤著书，历时约十四年完成了《史记》。全书一百三十篇，记载了从黄帝到汉武帝三千多年的历史。")
                .build(),

            // 三国两晋南北朝
            EventEntity.builder()
                .uid("guandu").title("官渡之战").year(200)
                .yearDisplay("公元200年").yearPrecision("exact")
                .category("战争")
                .tags(Arrays.asList("三国", "曹操"))
                .description("曹操以少胜多，在官渡大败袁绍，奠定了统一北方的基础。")
                .fulltext("建安五年，曹操率军与袁绍在官渡对峙。曹操采纳许攸之计，奇袭乌巢粮仓，一举击溃袁绍主力，为日后统一北方奠定基础。")
                .build(),
            EventEntity.builder()
                .uid("chibi").title("赤壁之战").year(208)
                .yearDisplay("公元208年").yearPrecision("exact")
                .category("战争")
                .tags(Arrays.asList("三国", "孙权", "刘备"))
                .description("孙刘联军在赤壁以火攻大败曹操八十三万大军，奠定了三国鼎立的基础。")
                .fulltext("建安十三年，曹操率大军南下，欲一统天下。孙权与刘备联合，周瑜率军在赤壁以火攻大破曹军。此战成为中国历史上最著名的以少胜多的战役之一。")
                .build(),
            EventEntity.builder()
                .uid("san-guo-fen-li").title("三国鼎立").year(220)
                .yearDisplay("公元220年").yearPrecision("exact")
                .category("朝代更迭")
                .tags(Arrays.asList("三国", "鼎立"))
                .description("曹丕篡汉建魏，刘备称帝建蜀，孙权称帝建吴，三国鼎立局面正式形成。")
                .fulltext("公元220年曹丕代汉建魏，221年刘备在成都称帝建蜀汉，229年孙权在建业称帝建东吴。三国鼎立局面正式形成，开始了长达六十年的分裂对峙。")
                .build(),
            EventEntity.builder()
                .uid("yi-de-jing").title("夷陵之战").year(222)
                .yearDisplay("公元222年").yearPrecision("exact")
                .category("战争")
                .tags(Arrays.asList("三国", "火烧连营"))
                .description("陆逊在夷陵以火攻大败刘备，奠定了三国鼎立的格局。")
                .fulltext("章武二年，刘备为报关羽之仇率军伐吴，连营七百里。陆逊坚守不战，待蜀军疲惫后以火攻击溃蜀军，刘备退守白帝城。")
                .build(),
            EventEntity.builder()
                .uid("sui-unify").title("隋朝统一全国").year(589)
                .yearDisplay("公元589年").yearPrecision("exact")
                .category("朝代更迭")
                .tags(Arrays.asList("隋朝", "统一"))
                .description("隋文帝杨坚灭陈朝，结束了自西晋末年以来近三百年的分裂局面。")
                .fulltext("公元581年杨坚代周建隋，589年派晋王杨广率军南下灭陈，结束了自永嘉之乱以来近三百年的分裂局面。")
                .build(),
            EventEntity.builder()
                .uid("sui-ke-ju").title("科举制度创立").year(605)
                .yearDisplay("公元605年").yearPrecision("exact")
                .category("改革")
                .tags(Arrays.asList("隋朝", "科举"))
                .description("隋炀帝创立进士科，标志着科举制度的诞生，影响了中国一千三百多年。")
                .fulltext("大业元年，隋炀帝创立进士科，通过考试选拔人才，打破了门阀世族的垄断，使平民子弟有机会通过读书改变命运。")
                .build(),
            EventEntity.builder()
                .uid("sui-da-yun").title("大运河开凿").year(605)
                .yearDisplay("公元605年").yearPrecision("exact")
                .category("盛世")
                .tags(Arrays.asList("隋朝", "工程"))
                .description("隋炀帝征发百万民夫开凿大运河，连接五大水系，成为南北交通大动脉。")
                .fulltext("大业元年，隋唐帝下令开凿大运河，以洛阳为中心，北抵涿郡，南至余杭，全长约2700公里，是世界古代最长的运河工程。")
                .build(),
            EventEntity.builder()
                .uid("an-shi").title("安史之乱").year(755)
                .yearDisplay("公元755年").yearPrecision("exact")
                .category("屈辱")
                .tags(Arrays.asList("唐朝", "叛乱"))
                .description("安禄山、史思明发动叛乱，持续八年之久，唐朝由盛转衰。")
                .fulltext("天宝十四载，安禄山在范阳起兵叛乱，叛军席卷中原。此乱持续八年，唐朝元气大伤，藩镇割据局面形成。")
                .build(),

            // 宋元明清新增事件
            EventEntity.builder()
                .uid("jing-kang").title("靖康之变").year(1127)
                .yearDisplay("公元1127年").yearPrecision("exact")
                .category("屈辱")
                .tags(Arrays.asList("宋朝", "金国"))
                .description("金军攻破汴京，俘虏徽钦二帝，北宋灭亡。")
                .fulltext("靖康二年，金军攻破汴京，俘虏宋徽宗、宋钦宗及宗室、大臣数千人北去，北宋灭亡。康王赵构南渡建立南宋。")
                .build(),
            EventEntity.builder()
                .uid("yue-fei").title("岳飞抗金").year(1134)
                .yearDisplay("约公元1134年").yearPrecision("approx")
                .category("战争")
                .tags(Arrays.asList("宋朝", "岳飞"))
                .description("岳飞率岳家军英勇抗金，屡建战功，最终被秦桧以莫须有罪名杀害。")
                .fulltext("岳飞字鹏举，南宋抗金名将。他率领岳家军多次击败金军，收复失地。但最终被秦桧以莫须有罪名杀害于风波亭，成为千古冤案。")
                .build(),
            EventEntity.builder()
                .uid("yuan-establish").title("元朝建立").year(1271)
                .yearDisplay("公元1271年").yearPrecision("exact")
                .category("朝代更迭")
                .tags(Arrays.asList("元朝", "忽必烈"))
                .description("忽必烈改国号为元，建立元朝，后灭南宋统一全国。")
                .fulltext("至元八年，忽必烈取《易经》'大哉乾元'之意，改国号为元。至元十六年灭南宋，统一全国，建立了中国历史上版图最大的王朝。")
                .build(),
            EventEntity.builder()
                .uid("ming-ji-si").title("土木堡之变").year(1449)
                .yearDisplay("公元1449年").yearPrecision("exact")
                .category("屈辱")
                .tags(Arrays.asList("明朝", "瓦剌"))
                .description("明英宗亲征瓦剌被俘，明朝由盛转衰。")
                .fulltext("正统十四年，瓦剌也先率军南下，明英宗朱祁镇在王振怂恿下亲征，在土木堡全军覆没被俘。后于谦拥立景泰帝，坚守北京。")
                .build(),
            EventEntity.builder()
                .uid("opium-war").title("鸦片战争").year(1840)
                .yearDisplay("公元1840年").yearPrecision("exact")
                .category("屈辱")
                .tags(Arrays.asList("清朝", "近代"))
                .description("英国以林则徐虎门销烟为借口发动战争，中国战败签订《南京条约》，开始沦为半殖民地半封建社会。")
                .fulltext("道光二十年，英国以禁烟为由派舰队进攻广东。清军战败，被迫签订《南京条约》，割让香港岛，开放五口通商，中国开始沦为半殖民地半封建社会。")
                .build(),
            EventEntity.builder()
                .uid("xin-hai-ge-ming").title("辛亥革命").year(1911)
                .yearDisplay("公元1911年").yearPrecision("exact")
                .category("革命")
                .tags(Arrays.asList("清朝", "共和"))
                .description("武昌起义爆发，推翻了清王朝统治，结束了中国两千多年的封建帝制。")
                .fulltext("宣统三年八月十九（1911年10月10日），武昌新军工程营开始起义枪声。短短两个月内，全国十六个省相继宣布独立，清王朝土崩瓦解。1912年1月1日，中华民国成立，孙中山就任临时大总统。")
                .build()
        );

        eventRepo.saveAll(events);
        log.info("初始化事件数据：{} 条", events.size());
    }

    private void initPersons() {
        if (personRepo.count() > 0) return;

        List<PersonEntity> persons = Arrays.asList(
            PersonEntity.builder()
                .uid("confucius").name("孔子").courtesyName("")
                .years(Arrays.asList(-551, -479))
                .yearsDisplay("前551年—前479年")
                .gender("male")
                .roles(Arrays.asList("思想家", "教育家", "政治家"))
                .tags(Arrays.asList("儒家", "春秋", "万世师表"))
                .quote("学而时习之，不亦说乎？有朋自远方来，不亦乐乎？")
                .bio("孔子，名丘，字仲尼，春秋时期鲁国人。中国古代伟大的思想家、教育家，儒家学派创始人。他创办私学，主张'有教无类'，整理了《诗》《书》《礼》《乐》《易》《春秋》六经。他的言行被弟子记录在《论语》中，对中国乃至世界文化产生了深远影响。")
                .build(),

            PersonEntity.builder()
                .uid("laizi").name("老子").courtesyName("")
                .years(Arrays.asList(null, null))
                .yearsDisplay("约公元前6世纪")
                .gender("male")
                .roles(Arrays.asList("思想家", "哲学家"))
                .tags(Arrays.asList("道家", "春秋", "道德经"))
                .quote("道可道，非常道；名可名，非常名。")
                .bio("老子，姓李名耳，字聃，春秋时期楚国苦县人。中国上古伟大的思想家、哲学家，道家学派创始人。著有《道德经》（又称《老子》），提出了'道'这一核心哲学概念，主张无为而治、顺应自然。")
                .build(),

            PersonEntity.builder()
                .uid("sunzi").name("孙武").courtesyName("")
                .years(Arrays.asList(-545, -470))
                .yearsDisplay("约前545年—约前470年")
                .gender("male")
                .roles(Arrays.asList("军事家", "将领"))
                .tags(Arrays.asList("兵法", "春秋", "吴国"))
                .quote("知己知彼，百战不殆。")
                .bio("孙武，字长卿，齐国乐安人，后流亡至吴国。春秋时期著名军事家、政治家。著有《孙子兵法》，是世界上最早的军事著作之一，被尊为'兵圣'。其军事思想影响深远，被翻译成多种语言，流传于世。")
                .build(),

            PersonEntity.builder()
                .uid("qin-shi-huang").name("嬴政").courtesyName("")
                .years(Arrays.asList(-259, -210))
                .yearsDisplay("前259年—前210年")
                .gender("male")
                .roles(Arrays.asList("政治家", "帝王"))
                .tags(Arrays.asList("秦朝", "统一", "始皇帝"))
                .quote("朕为始皇帝，后世以计数，二世三世至于万世，传之无穷。")
                .bio("嬴政，秦庄襄王之子，中国历史上第一位皇帝。公元前247年即位秦王，前221年统一六国，建立秦朝。推行郡县制、统一文字度量衡、修筑万里长城，奠定了中国两千多年政治制度的基础。")
                .build(),

            PersonEntity.builder()
                .uid("liu-bang").name("刘邦").courtesyName("")
                .years(Arrays.asList(-256, -195))
                .yearsDisplay("前256年—前195年")
                .gender("male")
                .roles(Arrays.asList("政治家", "帝王"))
                .tags(Arrays.asList("汉朝", "开国", "沛公"))
                .quote("大风起兮云飞扬，威加海内兮归故乡，安得猛士兮守四方！")
                .bio("刘邦，字季，沛县丰邑人。秦末农民起义领袖，汉朝开国皇帝。早年任泗水亭长，陈胜起义后起兵反秦，先入关中灭秦。后在楚汉之争中击败项羽，建立西汉王朝，开创了四百年的大汉基业。")
                .build(),

            PersonEntity.builder()
                .uid("wu-di").name("刘彻").courtesyName("")
                .years(Arrays.asList(-156, -87))
                .yearsDisplay("前156年—前87年")
                .gender("male")
                .roles(Arrays.asList("帝王", "政治家"))
                .tags(Arrays.asList("汉朝", "汉武帝", "雄才大略"))
                .quote("塞外茫茫无终点，人生何处不相逢。")
                .bio("刘彻，汉景帝之子，西汉第七位皇帝。在位五十四年，是汉朝在位时间最长的皇帝。他独尊儒术，开疆拓土，派张骞出使西域，派卫青霍去病北击匈奴，使汉朝成为当时世界上最强大的帝国之一。")
                .build(),

            PersonEntity.builder()
                .uid("simian-qian").name("司马迁").courtesyName("")
                .years(Arrays.asList(-145, -86))
                .yearsDisplay("约前145年—约前86年")
                .gender("male")
                .roles(Arrays.asList("史学家", "文学家"))
                .tags(Arrays.asList("汉朝", "史记", "太史公"))
                .quote("人固有一死，或重于泰山，或轻于鸿毛。")
                .bio("司马迁，字子长，夏阳人。西汉著名史学家、文学家。任太史令后因李陵事件获罪受宫刑。忍辱负重，发愤著书，历时约十四年完成中国第一部纪传体通史《史记》，被鲁迅誉为'史家之绝唱，无韵之离骚'。")
                .build(),

            PersonEntity.builder()
                .uid("zhugeliang").name("诸葛亮").courtesyName("孔明")
                .years(Arrays.asList(181, 234))
                .yearsDisplay("181年—234年")
                .gender("male")
                .roles(Arrays.asList("政治家", "军事家", "文学家"))
                .tags(Arrays.asList("三国", "蜀汉", "卧龙"))
                .quote("鞠躬尽瘁，死而后已。")
                .bio("诸葛亮，字孔明，琅琊阳都人。三国时期蜀汉丞相，中国古代杰出的政治家、军事家。隐居隆中时提出《隆中对》，后辅佐刘备建立蜀汉政权。刘备死后受托孤辅佐刘禅，多次北伐曹魏，最终病逝于五丈原。")
                .build(),

            PersonEntity.builder()
                .uid("libai").name("李白").courtesyName("太白")
                .years(Arrays.asList(701, 762))
                .yearsDisplay("701年—762年")
                .gender("male")
                .roles(Arrays.asList("诗人"))
                .tags(Arrays.asList("唐朝", "诗仙", "浪漫主义"))
                .quote("天生我材必有用，千金散尽还复来。")
                .bio("李白，字太白，号青莲居士。唐代伟大的浪漫主义诗人，被后人誉为'诗仙'。与杜甫并称'李杜'。其诗风豪放飘逸，想象丰富奇特，语言流转自然。代表作有《将进酒》《望庐山瀑布》《静夜思》等，流传至今近千首。")
                .build(),

            PersonEntity.builder()
                .uid("dufu").name("杜甫").courtesyName("子美")
                .years(Arrays.asList(712, 770))
                .yearsDisplay("712年—770年")
                .gender("male")
                .roles(Arrays.asList("诗人"))
                .tags(Arrays.asList("唐朝", "诗圣", "现实主义"))
                .quote("安得广厦千万间，大庇天下寒士俱欢颜。")
                .bio("杜甫，字子美，自号少陵野老。唐代伟大的现实主义诗人，被后世尊为'诗圣'。与李白并称'李杜'。其诗多涉笔社会动荡、政治黑暗、人民疾苦，被誉为'诗史'。代表作有《登高》《春望》《茅屋为秋风所破歌》等。")
                .build(),

            PersonEntity.builder()
                .uid("xuanzang").name("玄奘").courtesyName("")
                .years(Arrays.asList(602, 664))
                .yearsDisplay("602年—664年")
                .gender("male")
                .roles(Arrays.asList("佛学家", "旅行家", "翻译家"))
                .tags(Arrays.asList("唐朝", "西游记", "佛教"))
                .quote("宁可就西而死，岂东生而辱。")
                .bio("玄奘，俗姓陈，名祎，洛州缑氏人。唐代著名佛学家、旅行家、翻译家。贞观三年独自西行取经，历时十七年到达印度那烂陀寺学习。带回佛经六百五十七部，主持翻译了其中七十五部。其弟子记录其行程的《大唐西域记》是研究古代中亚和印度历史的珍贵文献。")
                .build(),

            PersonEntity.builder()
                .uid("zhaomei").name("武则天").courtesyName("")
                .years(Arrays.asList(624, 705))
                .yearsDisplay("624年—705年")
                .gender("female")
                .roles(Arrays.asList("政治家", "帝王"))
                .tags(Arrays.asList("唐朝", "女皇", "武周"))
                .quote("试看今日之域中，竟是谁家之天下！")
                .bio("武则天，名曌，并州文水人。中国历史上唯一的女皇帝。初为唐太宗才人，后入感业寺为尼。唐高宗时被封为皇后，逐渐掌握朝政。690年自立为帝，改国号为周，定都洛阳。在位期间大力发展科举，重用人才，为'开元盛世'奠定了基础。")
                .build(),

            PersonEntity.builder()
                .uid("cheng-jiguang").name("戚继光").courtesyName("元敬")
                .years(Arrays.asList(1528, 1588))
                .yearsDisplay("1528年—1588年")
                .gender("male")
                .roles(Arrays.asList("军事家", "将领"))
                .tags(Arrays.asList("明朝", "抗倭", "民族英雄"))
                .quote("封侯非我意，但愿海波平。")
                .bio("戚继光，字元敬，号南塘，明代著名军事家、民族英雄。在东南沿海抗击倭寇十余年，扫平了多年为虐沿海的倭患。后调北方防御蒙古，在北部边防十余年，使边备修饬，虏不敢犯。所著《纪效新书》《练兵实纪》为古代军事学重要著作。")
                .build(),

            PersonEntity.builder()
                .uid("lixizhi").name("李时珍").courtesyName("东璧")
                .years(Arrays.asList(1518, 1593))
                .yearsDisplay("1518年—1593年")
                .gender("male")
                .roles(Arrays.asList("医学家", "药学家"))
                .tags(Arrays.asList("明朝", "本草纲目", "医学"))
                .quote("医者仁心，济世救人。")
                .bio("李时珍，字东璧，号濒湖，蕲州人。明代著名医药学家。历时二十七年的心血，编撰完成《本草纲目》。全书收录药物一千八百九十二种，附图一千一百零九幅，是中国古代医药学的集大成之作，被达尔文称为'中国古代百科全书'。")
                .build(),

            PersonEntity.builder()
                .uid("kangxi").name("康熙帝").courtesyName("玄烨")
                .years(Arrays.asList(1654, 1722))
                .yearsDisplay("1654年—1722年")
                .gender("male")
                .roles(Arrays.asList("帝王", "政治家"))
                .tags(Arrays.asList("清朝", "康乾盛世", "仁政"))
                .quote("为君之道，必须以天地好生之德为主。")
                .bio("康熙帝，爱新觉罗·玄烨，清圣祖。中国历史上在位时间最长的皇帝（61年）。八岁登基，十四岁亲政。在位期间平定三藩、收复台湾、抗击沙俄、亲征葛尔丹，开创了'康乾盛世'的第一章。他勤政爱民，重视农桑，提倡儒学，是一位很有作为的君主。")
                .build(),

            PersonEntity.builder()
                .uid("sunzhongshan").name("孙中山").courtesyName("逸仙")
                .years(Arrays.asList(1866, 1925))
                .yearsDisplay("1866年—1925年")
                .gender("male")
                .roles(Arrays.asList("革命家", "政治家"))
                .tags(Arrays.asList("近代", "辛亥革命", "国父"))
                .quote("革命尚未成功，同志仍须努力。")
                .bio("孙中山，名文，字载之，号逸仙。中国近代民主革命的伟大先行者，中华民国和中国国民党的缔造者。他提出'三民主义'，领导辛亥革命推翻了清朝统治，结束了中国两千多年的封建帝制，建立了亚洲第一个共和国。")
                .build(),

            // 新增人物
            PersonEntity.builder()
                .uid("mengzi").name("孟子").courtesyName("")
                .years(Arrays.asList(-372, -289))
                .yearsDisplay("约前372年—约前289年")
                .gender("male")
                .roles(Arrays.asList("思想家", "教育家"))
                .tags(Arrays.asList("儒家", "战国", "亚圣"))
                .quote("民为贵，社稷次之，君为轻。")
                .bio("孟子，名轲，邹国人。战国时期著名思想家、教育家，儒家学派代表人物，被尊为'亚圣'。继承和发展了孔子的思想，提出'性善论'和'仁政'学说。")
                .build(),

            PersonEntity.builder()
                .uid("han-feizi").name("韩非").courtesyName("")
                .years(Arrays.asList(-280, -233))
                .yearsDisplay("约前280年—约前233年")
                .gender("male")
                .roles(Arrays.asList("思想家", "政治家"))
                .tags(Arrays.asList("法家", "战国"))
                .quote("法不阿贵，绳不挠曲。")
                .bio("韩非，韩国公子。战国末期著名思想家，法家学派集大成者。他将商鞅的法、申不害术、慎到的势融为一体，著有《韩非子》。")
                .build(),

            PersonEntity.builder()
                .uid("caocen").name("曹操").courtesyName("孟德")
                .years(Arrays.asList(155, 220))
                .yearsDisplay("155年—220年")
                .gender("male")
                .roles(Arrays.asList("政治家", "军事家", "文学家"))
                .tags(Arrays.asList("三国", "魏国", "枭雄"))
                .quote("老骥伏枥，志在千里。烈士暮年，壮心不已。")
                .bio("曹操，字孟德，沛国谯县人。三国时期曹魏政权的奠基人。他统一北方，推行屯田，恢复农业生产。同时是杰出的文学家，其诗作慷慨悲壮。")
                .build(),

            PersonEntity.builder()
                .uid("yuefei").name("岳飞").courtesyName("鹏举")
                .years(Arrays.asList(1103, 1142))
                .yearsDisplay("1103年—1142年")
                .gender("male")
                .roles(Arrays.asList("军事家", "民族英雄"))
                .tags(Arrays.asList("宋朝", "抗金", "岳家军"))
                .quote("莫等闲，白了少年头，空悲切。")
                .bio("岳飞，字鹏举，相州汤阴人。南宋著名军事家、民族英雄。他率领岳家军英勇抗金，收复失地，最终被秦桧以莫须有罪名杀害于风波亭。")
                .build(),

            PersonEntity.builder()
                .uid("wangyangming").name("王阳明").courtesyName("伯安")
                .years(Arrays.asList(1472, 1529))
                .yearsDisplay("1472年—1529年")
                .gender("male")
                .roles(Arrays.asList("思想家", "军事家", "教育家"))
                .tags(Arrays.asList("明朝", "心学", "知行合一"))
                .quote("知是行之始，行是知之成。")
                .bio("王阳明，名守仁，字伯安，号阳明。明代著名思想家、军事家。他创立了心学流派，提出'知行合一'和'致良知'的思想，对东亚文化影响深远。")
                .build(),

            PersonEntity.builder()
                .uid("qian-long").name("乾隆帝").courtesyName("弘历")
                .years(Arrays.asList(1711, 1799))
                .yearsDisplay("1711年—1799年")
                .gender("male")
                .roles(Arrays.asList("帝王", "政治家"))
                .tags(Arrays.asList("清朝", "康乾盛世"))
                .quote("十全老人")
                .bio("乾隆帝，爱新觉罗·弘历，清高宗。在位六十年，是中国历史上实际掌权时间最长的皇帝。开创了'康乾盛世'的最后辉煌，编纂《四库全书》。")
                .build(),

            PersonEntity.builder()
                .uid("ci-xi").name("慈禧太后").courtesyName("")
                .years(Arrays.asList(1835, 1908))
                .yearsDisplay("1835年—1908年")
                .gender("female")
                .roles(Arrays.asList("政治家"))
                .tags(Arrays.asList("清朝", "晚清", "太后"))
                .quote("朕即国家")
                .bio("慈禧太后，叶赫那拉氏。晚清实际统治者，执政近半个世纪。她镇压太平天国、推行洋务运动，但也扼杀戊戌变法，是晚清最具争议的政治人物。")
                .build(),

            PersonEntity.builder()
                .uid("caoe-yuan").name("曹操").courtesyName("孟德")
                .years(Arrays.asList(155, 220))
                .yearsDisplay("155年—220年")
                .gender("male")
                .roles(Arrays.asList("政治家", "军事家", "文学家"))
                .tags(Arrays.asList("三国", "魏武帝"))
                .quote("对酒当歌，人生几何。")
                .bio("曹操，三国时期杰出的政治家、军事家和文学家。统一北方，推行屯田，其诗作《短歌行》《观沧海》流传千古。")
                .build()
        );

        personRepo.saveAll(persons);
        log.info("初始化人物数据：{} 条", persons.size());
    }

    private void initKnowledgeCards() {
        if (knowledgeCardRepo.count() > 0) return;

        List<KnowledgeCardEntity> cards = Arrays.asList(
            KnowledgeCardEntity.builder()
                .uid("four-inventions").title("四大发明").startYear(-200)
                .startYearDisplay("约公元前200年—公元15世纪")
                .tags(Arrays.asList("科技", "发明", "文化"))
                .description("造纸术、印刷术、火药和指南针是中国古代四大发明，对世界文明进程产生了深远影响。")
                .fulltext("四大发明是中国古代最具代表性的科技创新成果。造纸术（西汉）和印刷术（隋唐）推动了知识的传播；火药（唐宋）改变了战争形态；指南针（战国至宋）促进了航海事业的发展。这些发明经由丝绸之路传入欧洲，为文艺复兴和地理大发现提供了重要条件。")
                .meta("四大发明具体包括：造纸术（蔡伦改进）、印刷术（毕昇活字）、火药（炼丹术偶然发现）、指南针（司南）")
                .build(),

            KnowledgeCardEntity.builder()
                .uid("sil-road").title("丝绸之路").startYear(-138)
                .startYearDisplay("公元前138年开通")
                .tags(Arrays.asList("贸易", "外交", "文化"))
                .description("丝绸之路是古代连接中国与中亚、西亚、欧洲的商贸通道，促进了东西方经济文化交流。")
                .fulltext("张骞出使西域后，丝绸之路正式开通。这条路线从长安出发，经河西走廊、新疆，穿越中亚、波斯，最终抵达地中海沿岸。丝绸、瓷器、茶叶等中国商品沿此路西传，葡萄、胡桃、佛教等外来文化传入中原。丝绸之路不仅是商贸通道，更是文明交流的桥梁。")
                .meta("丝绸之路分为陆上丝绸之路和海上丝绸之路两大路线")
                .build(),

            KnowledgeCardEntity.builder()
                .uid("keju-system").title("科举制度").startYear(605)
                .startYearDisplay("公元605年（隋炀帝）")
                .tags(Arrays.asList("制度", "教育", "选拔"))
                .description("科举制度是中国古代通过考试选拔官吏的制度，从隋朝创立到清朝废除，延续了一千三百多年。")
                .fulltext("隋炀帝大业元年（605年）正式创立进士科，标志着科举制度的诞生。唐朝进一步完善，宋代增加录取名额并实行糊名誊录制度。科举考试分为童试、乡试、会试、殿试四级，考中者分别称秀才、举人、进士、状元。科举制度打破了门阀世族的垄断，使平民子弟有机会通过读书改变命运。")
                .meta("科举制度于1905年被清政府废除，对中国社会产生了深远影响")
                .build(),

            KnowledgeCardEntity.builder()
                .uid("chinese-character").title("汉字演变").startYear(-1200)
                .startYearDisplay("约公元前1200年（甲骨文）")
                .tags(Arrays.asList("文化", "文字", "艺术"))
                .description("汉字是世界上最古老的文字之一，经历了甲骨文、金文、篆书、隶书、楷书等多个阶段的演变。")
                .fulltext("汉字起源于约公元前1200年的甲骨文，最初是刻在龟甲兽骨上的占卜文字。随后发展为铸在青铜器上的金文（钟鼎文）。秦统一后推行小篆，汉代演变为隶书，魏晋时期楷书定型。汉字的演变历程反映了中华文明的传承与发展，至今仍在使用。")
                .meta("汉字属于表意文字系统，是世界上唯一仍在使用的古老文字")
                .build(),

            KnowledgeCardEntity.builder()
                .uid("spring-autumn-warring").title("春秋战国").startYear(-770)
                .startYearDisplay("公元前770年—前221年")
                .tags(Arrays.asList("历史时期", "思想", "变革"))
                .description("春秋战国时期是中国历史上最重要的转型期之一，思想文化空前繁荣，出现了'百家争鸣'的局面。")
                .fulltext("春秋时期（前770—前476年）和战国时期（前475—前221年）合称春秋战国。这一时期周王室衰微，诸侯争霸，社会剧烈变革。思想文化方面出现了'百家争鸣'：儒家、道家、法家、墨家、兵家等各派思想家纷纷涌现，奠定了中国传统文化的基础。")
                .meta("春秋战国时期涌现了孔子、老子、墨子、孟子、庄子、韩非子等伟大思想家")
                .build(),

            KnowledgeCardEntity.builder()
                .uid("great-grand-can").title("大运河").startYear(-486)
                .startYearDisplay("公元前486年（最早开凿）")
                .tags(Arrays.asList("工程", "交通", "经济"))
                .description("京杭大运河是世界上里程最长、工程最大的古代运河，连接海河、黄河、淮河、长江、钱塘江五大水系。")
                .fulltext("大运河的开凿始于春秋时期吴王夫差开凿邗沟。隋朝大规模扩建，以洛阳为中心，北抵涿郡（今北京），南至余杭（今杭州）。元朝裁弯取直，形成今天的京杭大运河，全长约1794公里。")
                .meta("京杭大运河于2014年被列入世界文化遗产名录")
                .build(),

            KnowledgeCardEntity.builder()
                .uid("bai-jia-zheng-ming").title("百家争鸣").startYear(-500)
                .startYearDisplay("公元前500年—前300年")
                .tags(Arrays.asList("思想", "文化", "哲学"))
                .description("春秋战国时期各种思想流派纷纷涌现，形成了中国历史上第一次思想解放运动。")
                .fulltext("春秋战国时期，社会剧烈变革，各派思想家纷纷著书立说，形成了儒家、道家、法家、墨家、兵家、名家、阴阳家等学派。他们围绕治国之道、人性善恶、天人关系等话题展开激烈辩论，奠定了中国传统文化的基础。")
                .meta("百家争鸣持续约两百年，涌现了孔子、老子、墨子、孟子、庄子、韩非子等伟大思想家")
                .build(),

            KnowledgeCardEntity.builder()
                .title("中国古代四大美女").startYear(-500)
                .startYearDisplay("约公元前500年起")
                .tags(Arrays.asList("文化", "传说", "美学"))
                .description("西施、王昭君、貂蝉、杨玉环并称中国古代四大美女，各有'沉鱼落雁之容，闭月羞花之貌'。")
                .fulltext("四大美女分别对应'沉鱼'（西施）、'落雁'（王昭君）、'闭月'（貂蝉）、'羞花'（杨玉环）四个典故。她们不仅以美貌著称，更在历史进程中扮演了重要角色。")
                .meta("四大美女的典故最早见于汉代文献")
                .build(),

            KnowledgeCardEntity.builder()
                .title("中国古代十大名将").startYear(-500)
                .startYearDisplay("约公元前500年起")
                .tags(Arrays.asList("军事", "历史", "人物"))
                .description("孙武、白起、韩信、卫青、霍去病、李世民、李靖、岳飞、徐达、戚继光被公认为中国古代十大名将。")
                .fulltext("这十位名将跨越从春秋到明朝的漫长历史，各自在不同时期创造了辉煌的军事成就。孙武著《孙子兵法》，白起一生未尝一败，韩信'背水一战'，岳飞'撼山易，撼岳家军难'。")
                .meta("十大名将的评选参考了历代兵书和史书记载")
                .build(),

            KnowledgeCardEntity.builder()
                .title("中国十大古琴曲").startYear(-500)
                .startYearDisplay("约公元前500年起")
                .tags(Arrays.asList("音乐", "文化", "艺术"))
                .description("高山流水、广陵散、梅花三弄、夕阳箫鼓等十大古琴曲代表了中国古典音乐的巅峰。")
                .fulltext("古琴是中国最古老的弹拨乐器之一，有着三千多年的历史。十大古琴曲中，《高山流水》讲述了伯牙子期的知音故事，《广陵散》与聂政刺韩王的传说有关，《梅花三弄》则以梅花的高洁品格为主题。")
                .meta("古琴艺术于2003年被联合国教科文组织列为人类口头和非物质遗产代表作")
                .build(),

            KnowledgeCardEntity.builder()
                .title("二十四节气").startYear(-100)
                .startYearDisplay("约公元前100年定型")
                .tags(Arrays.asList("天文", "农业", "文化"))
                .description("二十四节气是中国古代订立的一种用来指导农事的补充历法，反映了太阳周年运动。")
                .fulltext("二十四节气起源于黄河流域，包括立春、雨水、惊蛰、春分、清明、谷雨、立夏、小满、芒种、夏至、小暑、大暑、立秋、处暑、白露、秋分、寒露、霜降、立冬、小雪、大雪、冬至、小寒、大寒。2016年被列入联合国教科文组织人类非物质文化遗产。")
                .meta("二十四节气完整反映了四季更替和气候变化规律")
                .build()
        );

        knowledgeCardRepo.saveAll(cards);
        log.info("初始化知识卡片数据：{} 条", cards.size());
    }
}
