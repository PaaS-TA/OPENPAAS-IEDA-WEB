package org.openpaas.ieda.web.config.stemcell.dao;
 
import java.util.List;
 
import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.web.config.stemcell.dto.StemcellManagementDTO;
 
public interface StemcellManagementDAO {
     
    /***************************************************
     * @project          : Paas 플랫폼 설치 자동화
     * @description   : Public Stemcell 목록 저장
     * @title               : insertPublicStemcells
     * @return            : int
    ***************************************************/
    int insertPublicStemcells(@Param("publicStemcells")List<StemcellManagementVO> publicStemcells);
     
    /***************************************************
     * @project          : Paas 플랫폼 설치 자동화
     * @description   : Public Stemcell 전체 삭제
     * @title               : deletePublicStemcells
     * @return            : int
    ***************************************************/
    int deletePublicStemcells();
     
    /***************************************************
     * @project          : Paas 플랫폼 설치 자동화
     * @description   : Public Stemcell 레코드 수 조회
     * @title               : selectCount
     * @return            : int
    ***************************************************/
    int selectCount();
     
    /***************************************************
     * @project          : Paas 플랫폼 설치 자동화
     * @description   : Public Stemcell 목록 조회
     * @title               : selectPublicStemcellList
     * @return            : List<StemcellManagementVO>
    ***************************************************/
    List<StemcellManagementVO> selectPublicStemcellList(  @Param("os")String operatingSystem, 
    		@Param("osVersion")String osVersion, @Param("iaas")String iaas); 
     
    /***************************************************
     * @project          : Paas 플랫폼 설치 자동화
     * @description   : 스템셀 파일명 조회(Os 버전 정렬)
     * @title               : selectStemcellFileNameOrderByOsVersion
     * @return            : List<StemcellManagementVO>
    ***************************************************/
    List<StemcellManagementVO> selectStemcellFileNameOrderByOsVersion(@Param("stemcellFileName")List<String> stemcellFileName);
     
    /***************************************************
     * @project          : Paas 플랫폼 설치 자동화
     * @description   : 스템셀 파일명 조회(스템셀 버전 정렬)
     * @title               : selectStemcellFileNameOrderByStemcellVersion
     * @return            : List<StemcellManagementVO>
    ***************************************************/
    List<StemcellManagementVO> selectStemcellFileNameOrderByStemcellVersion(@Param("stemcellFileName")List<String> stemcellFileName);
     
    /***************************************************
     * @project          : Paas 플랫폼 설치 자동화
     * @description   : 다운로드 상태 저장
     * @title               : insertDownloadStatusById
     * @return            : void
    ***************************************************/
    void insertDownloadStatusById(@Param("stemcell")StemcellManagementDTO.Download dto);
     
    /***************************************************
     * @project          : Paas 플랫폼 설치 자동화
     * @description   : 로컬에 다운로드 된 스템셀 조회
     * @title               : selectLocalStemcellList
     * @return            : List<StemcellManagementVO>
    ***************************************************/
    List<StemcellManagementVO> selectLocalStemcellList(@Param("iaas") String iaas);
     
    /***************************************************
     * @project          : Paas 플랫폼 설치 자동화
     * @description   : 다운로드 상태 수정
     * @title               : updateDownloadStatusById
     * @return            : void
    ***************************************************/
    void updateDownloadStatusById(String id);
 
    /***************************************************
     * @project          : Paas 플랫폼 설치 자동화
     * @description   : 해당 스템셀 파일명과 일치한 다운로드 상태 저장
     * @title               : updateDownloadStatusByStemcellFileName
     * @return            : void
    ***************************************************/
    void updateDownloadStatusByStemcellFileName(@Param("stemcellFileName") String stemcellFileName, @Param("downStatus")String downStatus);
}