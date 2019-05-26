package eu.rudisch.users.persistance;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import eu.rudisch.users.persistance.model.UserDetail;

@ExtendWith(MockitoExtension.class)
public class SqlServiceImplTest {

	@Mock
	UserDetailDao userDetailDao;

	@InjectMocks
	SqlServiceImpl sqlServiceImpl;

//	@BeforeEach
//	void init() {
//		MockitoAnnotations.initMocks(sqlService);
//	}

	@Test
	void shouldDeleteOneUser() {
		UserDetail userDetail = new UserDetail();
		userDetail.setId(1);

		when(sqlServiceImpl.getUserDetailById(1)).thenReturn(userDetail);
//		doAnswer(new Answer<Void>() {
//			public Void answer(InvocationOnMock invocation) {
//				sqlServiceImpl.remove(userDetail);
//				return null;
//			}
//		}).when(sqlServiceImpl).removeById(1);
		sqlServiceImpl.removeById(1);
		verify(sqlServiceImpl, times(1)).remove(userDetail);
	}

}
