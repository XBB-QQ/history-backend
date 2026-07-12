-- 为 50 位历史人物补充深度字段：
-- birth_place（出生地）、death_place（逝世地）、
-- achievements（主要成就 JSON 数组）、relationships（人物关系 JSON）
-- relationships 格式：[{"targetUid":"xxx","relation":"xxx","label":"xxx"}]

-- 思想家
UPDATE persons SET
    birth_place = '鲁国陬邑（今山东曲阜）',
    death_place = '鲁国曲阜',
    achievements = '["创立儒家学派","首创私学有教无类","编订六经（诗书礼乐易春秋）","被尊为至圣先师"]',
    relationships = '[{"targetUid":"mencius","relation":"后学","label":"孟子（继承发扬）"},{"targetUid":"laozi","relation":"同时代","label":"老子（问礼于老子）"}]'
WHERE uid = 'confucius';

UPDATE persons SET
    birth_place = '楚国苦县（今河南鹿邑）',
    death_place = '不详',
    achievements = '["创立道家学派","著《道德经》五千言","提出道法自然无为而治","被尊为太上老君"]',
    relationships = '[{"targetUid":"confucius","relation":"同时代","label":"孔子（问礼于老子）"},{"targetUid":"zhuangzi","relation":"后学","label":"庄子（继承发展）"}]'
WHERE uid = 'laozi';

UPDATE persons SET
    birth_place = '邹国（今山东邹城）',
    death_place = '邹国',
    achievements = '["继承发扬儒家思想被尊亚圣","提出性善论","创立仁政学说","强调民本思想"]',
    relationships = '[{"targetUid":"confucius","relation":"后学","label":"孔子（继承其学）"}]'
WHERE uid = 'mencius';

UPDATE persons SET
    birth_place = '宋国蒙（今河南商丘）',
    death_place = '宋国蒙',
    achievements = '["继承发展道家思想","著《庄子》三十三篇","提出逍遥齐物境界","寓言文学成就极高"]',
    relationships = '[{"targetUid":"laozi","relation":"前驱","label":"老子（并称老庄）"}]'
WHERE uid = 'zhuangzi';

UPDATE persons SET
    birth_place = '赵国（今山西临汾一带）',
    death_place = '楚国兰陵（今山东兰陵）',
    achievements = '["战国末期儒家代表","提出性恶论","强调礼法并重","弟子韩非李斯成法家代表"]',
    relationships = '[{"targetUid":"confucius","relation":"后学","label":"孔子（儒家传承）"},{"targetUid":"han-feizi","relation":"弟子","label":"韩非子（法家集大成）"},{"targetUid":"li-si","relation":"弟子","label":"李斯（秦丞相）"}]'
WHERE uid = 'xun-zi';

UPDATE persons SET
    birth_place = '宋国（今河南商丘一带）',
    death_place = '不详',
    achievements = '["创立墨家学派","提出兼爱非攻尚贤","发现小孔成像原理","研究力学几何学"]',
    relationships = '[{"targetUid":"confucius","relation":"对立","label":"孔子（儒墨显学之争）"}]'
WHERE uid = 'mozi';

UPDATE persons SET
    birth_place = '韩国（今河南新郑）',
    death_place = '秦国咸阳',
    achievements = '["法家思想集大成者","融合法术势三家","著《韩非子》五十二篇","为秦统一提供理论基础"]',
    relationships = '[{"targetUid":"xun-zi","relation":"老师","label":"荀子（师从）"},{"targetUid":"li-si","relation":"同门","label":"李斯（同窗）"},{"targetUid":"qin-shi","relation":"君主","label":"秦始皇（被其赏识）"}]'
WHERE uid = 'han-feizi';

UPDATE persons SET
    birth_place = '浙江余姚',
    death_place = '江西南安',
    achievements = '["创立心学体系","提出知行合一致良知","平定宁王之乱","被誉为三不朽圣人"]',
    relationships = '[{"targetUid":"zhang-juzheng","relation":"后学","label":"张居正（后世影响）"}]'
WHERE uid = 'wang-yangming';

-- 帝王
UPDATE persons SET
    birth_place = '赵国邯郸（今河北邯郸）',
    death_place = '沙丘（今河北广宗）',
    achievements = '["横扫六国完成统一","废分封立郡县","统一文字度量衡车轨","修筑万里长城"]',
    relationships = '[{"targetUid":"li-si","relation":"丞相","label":"李斯（辅佐统一）"}]'
WHERE uid = 'qin-shi';

UPDATE persons SET
    birth_place = '武功（今陕西武功）',
    death_place = '长安（今陕西西安）',
    achievements = '["开创贞观之治","完善三省六部制","被尊为天可汗","奠定大唐基业"]',
    relationships = '[{"targetUid":"yan-li-ben","relation":"臣子","label":"阎立本（为其作画）"}]'
WHERE uid = 'tang-taizong';

UPDATE persons SET
    birth_place = '并州文水（今山西文水）',
    death_place = '洛阳上阳宫',
    achievements = '["中国唯一女皇帝","发展科举重用人才","开创武周政权","为开元盛世奠基"]',
    relationships = '[]'
WHERE uid = 'wu-zetian';

UPDATE persons SET
    birth_place = '北京',
    death_place = '北京畅春园',
    achievements = '["在位61年历代最长","平定三藩收复台湾","抗击沙俄亲征噶尔丹","开创康乾盛世"]',
    relationships = '[]'
WHERE uid = 'kangxi';

-- 诗人文学家
UPDATE persons SET
    birth_place = '碎叶城（今吉尔吉斯托克马克）',
    death_place = '安徽当涂',
    achievements = '["中国最伟大的浪漫主义诗人","被誉为诗仙","代表作将进酒蜀道难","与杜甫并称李杜"]',
    relationships = '[{"targetUid":"dufu","relation":"挚友","label":"杜甫（并称李杜）"},{"targetUid":"wang-wei","relation":"同时代","label":"王维（盛唐三大家）"}]'
WHERE uid = 'libai';

UPDATE persons SET
    birth_place = '巩县（今河南巩义）',
    death_place = '湖南耒阳',
    achievements = '["中国最伟大的现实主义诗人","被誉为诗圣","诗被誉为诗史","代表作三吏三别春望"]',
    relationships = '[{"targetUid":"libai","relation":"挚友","label":"李白（并称李杜）"}]'
WHERE uid = 'dufu';

UPDATE persons SET
    birth_place = '眉州眉山（今四川眉山）',
    death_place = '常州（今江苏常州）',
    achievements = '["北宋文学巨匠","开创豪放派词风","位列唐宋八大家","书法宋四家之一"]',
    relationships = '[]'
WHERE uid = 'sushi';

UPDATE persons SET
    birth_place = '河东蒲州（今山西永济）',
    death_place = '不详',
    achievements = '["山水田园诗派代表","被誉为诗佛","诗中有画画中有诗","精通诗文书画音乐"]',
    relationships = '[{"targetUid":"libai","relation":"同时代","label":"李白（盛唐三大家）"},{"targetUid":"dufu","relation":"同时代","label":"杜甫（盛唐三大家）"}]'
WHERE uid = 'wang-wei';

UPDATE persons SET
    birth_place = '河南新郑',
    death_place = '洛阳',
    achievements = '["唐代三大诗人之一","新乐府运动领袖","代表作长恨歌琵琶行","主张文章合为时而著"]',
    relationships = '[{"targetUid":"libai","relation":"前驱","label":"李白（继承唐诗传统）"},{"targetUid":"dufu","relation":"前驱","label":"杜甫（继承唐诗传统）"}]'
WHERE uid = 'bai-juyi';

UPDATE persons SET
    birth_place = '济南（今山东济南）',
    death_place = '临安（今浙江杭州）',
    achievements = '["中国最伟大女词人","开创易安体","与辛弃疾并称济南二安","著《金石录后序》"]',
    relationships = '[]'
WHERE uid = 'li-qingzhao';

UPDATE persons SET
    birth_place = '陈留圉（今河南杞县）',
    death_place = '不详',
    achievements = '["中国杰出女诗人","著《悲愤诗》","作《胡笳十八拍》琴曲","整理蔡邕遗书四百余篇"]',
    relationships = '[]'
WHERE uid = 'cai-wenji';

-- 军事家
UPDATE persons SET
    birth_place = '齐国乐安（今山东广饶）',
    death_place = '不详',
    achievements = '["著《孙子兵法》世界最早兵书","辅佐吴王阖闾成就霸业","被尊为兵圣","七战七捷破楚入郢"]',
    relationships = '[]'
WHERE uid = 'sun-wu';

UPDATE persons SET
    birth_place = '河东平阳（今山西临汾）',
    death_place = '长安',
    achievements = '["西汉抗匈名将","七战七捷收复河套","官至大将军大司马","与霍去病并称西汉双璧"]',
    relationships = '[{"targetUid":"huo-qubing","relation":"外甥","label":"霍去病（并称西汉双璧）"}]'
WHERE uid = 'wei-qing';

UPDATE persons SET
    birth_place = '河东平阳（今山西临汾）',
    death_place = '长安',
    achievements = '["十七岁封冠军侯","六次出击匈奴歼敌十余万","收复河西走廊","封狼居胥登临瀚海"]',
    relationships = '[{"targetUid":"wei-qing","relation":"舅舅","label":"卫青（并称西汉双璧）"}]'
WHERE uid = 'huo-qubing';

UPDATE persons SET
    birth_place = '相州汤阴（今河南汤阴）',
    death_place = '临安大理寺风波亭',
    achievements = '["南宋最杰出抗金将领","组建岳家军纪律严明","郾城大捷破金军铁浮屠","被追谥武穆忠武"]',
    relationships = '[{"targetUid":"liang-hongyu","relation":"同朝","label":"梁红玉（同期抗金名将）"}]'
WHERE uid = 'yue-fei';

UPDATE persons SET
    birth_place = '山东登州（今山东蓬莱）',
    death_place = '山东蓬莱',
    achievements = '["明朝抗倭名将","组建戚家军","台州九战九捷平定倭患","著《纪效新书》军事理论"]',
    relationships = '[]'
WHERE uid = 'qi-ji-guang';

UPDATE persons SET
    birth_place = '淮阴（今江苏淮安）',
    death_place = '长安长乐宫',
    achievements = '["西汉开国功臣","被尊为兵仙","明修栈道暗度陈仓","背水一战十面埋伏"]',
    relationships = '[{"targetUid":"xiao-he","relation":"恩人","label":"萧何（月下追韩信）"}]'
WHERE uid = 'han-xin';

UPDATE persons SET
    birth_place = '淮安（今江苏淮安）',
    death_place = '不详',
    achievements = '["南宋抗金女英雄","黄天荡擂鼓战金山","以八百破金兵十万","巾帼不让须眉"]',
    relationships = '[{"targetUid":"yue-fei","relation":"同朝","label":"岳飞（同期抗金名将）"}]'
WHERE uid = 'liang-hongyu';

UPDATE persons SET
    birth_place = '不详（北方民间）',
    death_place = '不详',
    achievements = '["代父从军十二年","女扮男装屡立战功","成为中国女性英雄象征","《木兰辞》流传千古"]',
    relationships = '[]'
WHERE uid = 'hua-mulan';

-- 科学家
UPDATE persons SET
    birth_place = '南阳西鄂（今河南南阳）',
    death_place = '洛阳',
    achievements = '["发明候风地动仪世界最早","造浑天仪观测天象","著《灵宪》阐述宇宙理论","与司马相如扬雄并称汉代四大家"]',
    relationships = '[]'
WHERE uid = 'zhang-heng';

UPDATE persons SET
    birth_place = '范阳遒县（今河北涞水）',
    death_place = '建康（今南京）',
    achievements = '["将圆周率精确到小数点后七位","纪录保持千年","编制《大明历》引入岁差","著《缀术》创制千里船"]',
    relationships = '[]'
WHERE uid = 'zu-chongzhi';

UPDATE persons SET
    birth_place = '北宋淮南（今安徽一带）',
    death_place = '不详',
    achievements = '["发明胶泥活字印刷术","比欧洲古腾堡早四百年","被沈括《梦溪笔谈》记载","推动世界文明传播"]',
    relationships = '[{"targetUid":"shen-kuo","relation":"同时代","label":"沈括（记载其发明）"}]'
WHERE uid = 'bi-sheng';

UPDATE persons SET
    birth_place = '杭州钱塘（今浙江杭州）',
    death_place = '润州（今江苏镇江）',
    achievements = '["著《梦溪笔谈》百科全书","首创十二气历","发现磁偏角","研究光学声学地质学"]',
    relationships = '[{"targetUid":"bi-sheng","relation":"同时代","label":"毕昇（记载其发明）"}]'
WHERE uid = 'shen-kuo';

UPDATE persons SET
    birth_place = '奉新建县（今江西奉新）',
    death_place = '不详',
    achievements = '["著《天工开物》工艺百科","系统总结明代农业手工业","涵盖三十六个生产部门","被译多国文字影响世界"]',
    relationships = '[]'
WHERE uid = 'song-yingxing';

UPDATE persons SET
    birth_place = '邢台（今河北邢台）',
    death_place = '大都（今北京）',
    achievements = '["主持修订《授时历》","测定一年365.2425日","建造简仪仰仪等天文仪器","主持全国大地测量"]',
    relationships = '[]'
WHERE uid = 'guo-shou-jing';

-- 艺术家
UPDATE persons SET
    birth_place = '琅琊临沂（今山东临沂）',
    death_place = '会稽山阴（今浙江绍兴）',
    achievements = '["中国最伟大书法家世称书圣","《兰亭集序》被誉为天下第一行书","开创妍美流畅新书风","精研诸体自成一家"]',
    relationships = '[{"targetUid":"liu-gongquan","relation":"后学","label":"柳公权（继承发扬）"}]'
WHERE uid = 'wang-xi-zhi';

UPDATE persons SET
    birth_place = '阳翟（今河南禹州）',
    death_place = '不详',
    achievements = '["唐朝最伟大画家被尊画圣","创莼菜条线描技法","吴带当风传世","代表作《送子天王图》"]',
    relationships = '[]'
WHERE uid = 'wu-daozi';

UPDATE persons SET
    birth_place = '无锡（今江苏无锡）',
    death_place = '建康（今南京）',
    achievements = '["东晋杰出画家","提出以形写神理论","代表作《洛神赋图》","中国绘画理论奠基人"]',
    relationships = '[]'
WHERE uid = 'gu-kaizhi';

UPDATE persons SET
    birth_place = '雍州万年（今陕西西安）',
    death_place = '不详',
    achievements = '["唐初画家兼政治家","官至右丞相","代表作《步辇图》","作《历代帝王图》"]',
    relationships = '[{"targetUid":"tang-taizong","relation":"君主","label":"唐太宗（为其作画）"}]'
WHERE uid = 'yan-li-ben';

UPDATE persons SET
    birth_place = '京兆华原（今陕西铜川）',
    death_place = '不详',
    achievements = '["唐朝著名书法家","创柳体骨力遒劲","与颜真卿并称颜柳","代表作玄秘塔碑神策军碑"]',
    relationships = '[{"targetUid":"wang-xi-zhi","relation":"前驱","label":"王羲之（初学其书）"}]'
WHERE uid = 'liu-gongquan';

-- 政治家改革家
UPDATE persons SET
    birth_place = '卫国（今河南濮阳一带）',
    death_place = '秦国咸阳',
    achievements = '["在秦推行两次变法","废井田开阡陌","奖励耕战按军功授爵","奠定秦统一基础"]',
    relationships = '[]'
WHERE uid = 'shang-yang';

UPDATE persons SET
    birth_place = '琅琊阳都（今山东沂南）',
    death_place = '五丈原（今陕西宝鸡）',
    achievements = '["蜀汉丞相千古名相","联吴抗曹赤壁破曹","发明木牛流马孔明灯","《出师表》感人至深"]',
    relationships = '[{"targetUid":"cao-cao","relation":"对手","label":"曹操（赤壁之战对手）"},{"targetUid":"si-ma-yi","relation":"对手","label":"司马懿（北伐对手）"}]'
WHERE uid = 'zhuge-liang';

UPDATE persons SET
    birth_place = '抚州临川（今江西抚州）',
    death_place = '江宁（今江苏南京）',
    achievements = '["北宋著名政治家","主持熙宁变法","推行青苗法免役法","位列唐宋八大家"]',
    relationships = '[]'
WHERE uid = 'wang-anshi';

UPDATE persons SET
    birth_place = '江陵（今湖北荆州）',
    death_place = '北京',
    achievements = '["明朝万历内阁首辅","推行考成法整顿吏治","改革一条鞭法","开创万历中兴"]',
    relationships = '[]'
WHERE uid = 'zhang-juzheng';

UPDATE persons SET
    birth_place = '合肥（今安徽合肥）',
    death_place = '北京',
    achievements = '["晚清重臣淮军创始人","主持洋务运动三十年","创办江南制造总局北洋水师","签订多个不平等条约"]',
    relationships = '[]'
WHERE uid = 'li-hongzhang';

UPDATE persons SET
    birth_place = '沛国谯县（今安徽亳州）',
    death_place = '洛阳',
    achievements = '["统一北方推行屯田","官渡之战以少胜多","开创建安风骨","被追尊为魏武帝"]',
    relationships = '[{"targetUid":"zhuge-liang","relation":"对手","label":"诸葛亮（赤壁之战对手）"},{"targetUid":"si-ma-yi","relation":"臣子","label":"司马懿（曹魏权臣）"},{"targetUid":"sun-quan","relation":"对手","label":"孙权（三分天下）"}]'
WHERE uid = 'cao-cao';

UPDATE persons SET
    birth_place = '河内温县（今河南温县）',
    death_place = '洛阳',
    achievements = '["曹魏杰出军事家政治家","对抗诸葛亮北伐","平定公孙渊","高平陵之变夺权"]',
    relationships = '[{"targetUid":"zhuge-liang","relation":"对手","label":"诸葛亮（北伐对抗）"},{"targetUid":"cao-cao","relation":"君主","label":"曹操（辅佐曹魏）"}]'
WHERE uid = 'si-ma-yi';

UPDATE persons SET
    birth_place = '吴郡富春（今浙江富阳）',
    death_place = '建业（今南京）',
    achievements = '["东吴开国皇帝","赤壁之战大败曹操","开发江南派卫温达夷洲","在位24年三国最久"]',
    relationships = '[{"targetUid":"cao-cao","relation":"对手","label":"曹操（赤壁之战对手）"},{"targetUid":"zhuge-liang","relation":"盟友","label":"诸葛亮（孙刘联盟）"}]'
WHERE uid = 'sun-quan';

UPDATE persons SET
    birth_place = '沛县（今江苏沛县）',
    death_place = '长安',
    achievements = '["西汉开国第一功臣","留守关中保障粮饷","制定《九章律》","萧何月下追韩信"]',
    relationships = '[{"targetUid":"han-xin","relation":"推荐","label":"韩信（月下追荐）"}]'
WHERE uid = 'xiao-he';

UPDATE persons SET
    birth_place = '河东平阳（今山西临汾）',
    death_place = '长安',
    achievements = '["辅佐昭帝宣帝三朝","平定上官桀谋反","废昌邑王立汉宣帝","史称霍光废立"]',
    relationships = '[{"targetUid":"huo-qubing","relation":"异母弟","label":"霍去病（同父异母）"}]'
WHERE uid = 'huo-guang';

UPDATE persons SET
    birth_place = '楚国上蔡（今河南上蔡）',
    death_place = '咸阳',
    achievements = '["辅佐秦始皇统一六国","建议废分封行郡县","统一文字度量衡","著《谏逐客书》"]',
    relationships = '[{"targetUid":"qin-shi","relation":"君主","label":"秦始皇（辅佐统一）"},{"targetUid":"xun-zi","relation":"老师","label":"荀子（师从）"},{"targetUid":"han-feizi","relation":"同门","label":"韩非子（同窗）"}]'
WHERE uid = 'li-si';

UPDATE persons SET
    birth_place = '南郡秭归（今湖北秭归）',
    death_place = '匈奴（漠北）',
    achievements = '["主动请行和亲匈奴","促成汉匈半世纪和平","其墓被称为青冢","成为民族团结象征"]',
    relationships = '[]'
WHERE uid = 'wang-zhaojun';
