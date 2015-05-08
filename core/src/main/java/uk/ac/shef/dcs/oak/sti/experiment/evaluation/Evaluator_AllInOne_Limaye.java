package uk.ac.shef.dcs.oak.sti.experiment.evaluation;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: zqz
 * Date: 01/04/14
 * Time: 15:22
 * To change this template use File | Settings | File Templates.
 */
public class Evaluator_AllInOne_Limaye {
    public static void main(String[] args) throws IOException {
        Evaluator_EntityOnly ent_evaluator = new Evaluator_EntityOnly();
        Evaluator_ClassOnly cls_evaluator = new Evaluator_ClassOnly();
        Evaluator_RelationOnly rel_evaluator = new Evaluator_RelationOnly();
        /************************************************
         FORY limaye200
         *************************************************/
        String method = "smp";


        if (method.equals("nm")) {
            ent_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_nm+first(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\entity",
                    "tmp_result/limaye_entity_bs_nm.csv",
                    "tmp_result/limaye_entity_bs_nm_missed.csv",
                    true
            );
            cls_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_nm+first(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_header_bs_nm-all.csv",
                    "tmp_result/limaye_header_bs_nm-all_missed.csv", false
            );
            cls_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_nm+first(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_header_bs_nm-ne.csv",
                    "tmp_result/limaye_header_bs_nm-ne_missed.csv", true
            );
            rel_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_nm+first(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\relation",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_rel_bs_nm-ne.csv",
                    "tmp_result/limaye_rel_bs_nm_missed-ne.csv",true,true
            );
            rel_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_nm+first(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\relation",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_rel_bs_nm-all.csv",
                    "tmp_result/limaye_rel_bs_nm_missed-all.csv",true,false
            );

        } else if (method.equals("cos")) {
            ent_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_sl_cos(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\entity",
                    "tmp_result/limaye_entity_bs_cos.csv",
                    "tmp_result/limaye_entity_bs_cos_missed.csv",
                    true
            );
            cls_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_sl_cos(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_header_bs_cos-ne.csv",
                    "tmp_result/limaye_header_bs_cos-ne_missed.csv", true

            );
            cls_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_sl_cos(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_header_bs_cos-all.csv",
                    "tmp_result/limaye_header_bs_cos-all_missed.csv", false
            );
            rel_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_sl_cos(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\relation",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_rel_bs_cos-all.csv",
                    "tmp_result/limaye_rel_bs_cos_missed-all.csv",true,false
            );
            rel_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_sl_cos(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\relation",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_rel_bs_cos-ne.csv",
                    "tmp_result/limaye_rel_bs_cos_missed-ne.csv",true,true
            );
        } else if (method.equals("lev")) {
            ent_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_sl_lev(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\entity",
                    "tmp_result/limaye_entity_bs_sl_lev.csv",
                    "tmp_result/limaye_entity_bs_sl_lev_missed.csv",
                    true
            );
            cls_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_sl_lev(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_header_bs_sl_lev-all.csv",
                    "tmp_result/limaye_header_bs_sl_lev-all_missed.csv", false

            );
            cls_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_sl_lev(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_header_bs_sl_lev-ne.csv",
                    "tmp_result/limaye_header_bs_sl_lev-ne_missed.csv", true
            );
            rel_evaluator.evaluate(
                     "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_sl_lev(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\relation",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_rel_bs_lev-all.csv",
                    "tmp_result/limaye_rel_bs_lev_missed-all.csv",true,false
            );
            rel_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_sl_lev(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\relation",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_rel_bs_lev-ne.csv",
                    "tmp_result/limaye_rel_bs_lev_missed-ne.csv",true,true
            );
        } else if (method.equals("dice")) {
            ent_evaluator.evaluate(

                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_sl_dice(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\entity",
                    "tmp_result/limaye_entity_bs_sl_dice.csv",
                    "tmp_result/limaye_entity_bs_sl_dice_missed.csv",
                    true

            );
            cls_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_sl_dice(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_header_bs_sl_dice-all.csv",
                    "tmp_result/limaye_header_bs_sl_dice-all_missed.csv", false
            );
            cls_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_sl_dice(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_header_bs_sl_dice-ne.csv",
                    "tmp_result/limaye_header_bs_sl_dice-ne_missed.csv", true
            );
            rel_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_sl_dice(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\relation",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_rel_bs_dice-all.csv",
                    "tmp_result/limaye_rel_bs_dice_missed-all.csv",true,false
            );
            rel_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\baseline_sl_dice(RI)",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\relation",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_rel_bs_dice-ne.csv",
                    "tmp_result/limaye_rel_bs_dice_missed-ne.csv",true,true
            );

        } else if (method.equals("tm_ospd")) {
            ent_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\tm_dc_ri_ospd",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\entity",
                    "tmp_result/limaye_entity_tm_ospd.csv",
                    "tmp_result/limaye_entity_tm_ospd_missed.csv",
                    true
            );
            cls_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\tm_dc_ri_ospd",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_header_tm_ospd-all.csv",
                    "tmp_result/limaye_header_tm_ospd-all_missed.csv", false
            );
            cls_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\tm_dc_ri_ospd",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_header_tm_ospd-ne.csv",
                    "tmp_result/limaye_header_tm_ospd-ne_missed.csv", true

            );
            rel_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\tm_dc_ri_ospd",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\relation",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_rel_tm_ospd-all.csv",
                    "tmp_result/limaye_rel_tm_ospd_missed-all.csv",true,false
            );
            rel_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\output\\tm_dc_ri_ospd",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\relation",
                    "E:\\Data\\table_annotation\\limaye_sample\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_rel_tm_ospd-ne.csv",
                    "tmp_result/limaye_rel_tm_ospd_missed-ne.csv",true,true
            );
        } else if (method.equals("tm_ospd_nsc")) {
            /*ent_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\output\\datafiltering_tmsimple_2combined",
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\gs\\entity",
                    "tmp_result/limaye_entity_tm_ospd_nsc.csv",
                    "tmp_result/limaye_entity_tm_ospd_nsc_missed.csv",
                    true
            );
            cls_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\output\\datafiltering_tmsimple_2combined",
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_header_tm_ospd_nsc-ne.csv",
                    "tmp_result/limaye_header_tm_ospd_nsc-ne_missed.csv", true
            );*/

            ent_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\output\\datafiltering_tmsimple_2combined",
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\gs\\entity",
                    "tmp_result/limaye_entity_tm_ospd_nsc.csv",
                    "tmp_result/limaye_entity_tm_ospd_nsc_missed.csv",
                    true
            );
            cls_evaluator.evaluate_with_filter(
                    "D:\\Work\\lodie\\tmp_result\\ospd_tm_iswc\\limaye200/combined.log",
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\output\\datafiltering_tmsimple_2combined",
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_header_tm_ospd_nsc-ne.csv",
                    "tmp_result/limaye_header_tm_ospd_nsc-ne_missed.csv", true
            );
            /*cls_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\output\\datafiltering_tmsimple_random",
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_header_tm_ospd_nsc-all.csv",
                    "tmp_result/limaye_header_tm_ospd_nsc-all_missed.csv", false
            );
            rel_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\output\\datafiltering_tmsimple_random",
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\gs\\relation",
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_rel_tm_ospd_nsc-all.csv",
                    "tmp_result/limaye_rel_tm_ospd_nsc_missed-all.csv",true,false
            );
            rel_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\output\\datafiltering_tmsimple_random",
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\gs\\relation",
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_rel_tm_ospd_nsc-ne.csv",
                    "tmp_result/limaye_rel_tm_ospd_nsc_missed-ne.csv",true,true
            );*/
        }
        else if (method.equals("smp")) {
            ent_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\tmp",
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\gs\\entity",
                    "tmp_result/limaye_entity_smp.csv",
                    "tmp_result/limaye_entity_smp_missed.csv",
                    true
            );
            cls_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\tmp",
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_header_smp-all.csv",
                    "tmp_result/limaye_header_tm_smp-all_missed.csv", false
            );
            cls_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\tmp",
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_header_smp-ne.csv",
                    "tmp_result/limaye_header_smp-ne_missed.csv", true

            );
            rel_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\tmp",
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\gs\\relation",
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_rel_smp-all.csv",
                    "tmp_result/limaye_rel_smp_missed-all.csv", true, false
            );
            rel_evaluator.evaluate(
                    "E:\\Data\\table_annotation\\tmp",
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\gs\\relation",
                    "E:\\Data\\table_annotation\\limaye200\\200_tables_regen\\gs\\header_ne+prop",
                    "tmp_result/limaye_rel_smp-ne.csv",
                    "tmp_result/limaye_rel_smp_missed-ne.csv", true, true
            );
        }
        System.exit(0);
    }
}